#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
scan_repo.py
Сканирует репозиторий и создаёт упакованный отчёт (report_dir).
Пример запуска: python3 scan_repo.py --out report_dir
"""
import os, sys, argparse, hashlib, json, time, zipfile
from pathlib import Path

def short_hash(path):
    h = hashlib.sha256()
    try:
        with open(path,'rb') as f:
            while True:
                b = f.read(8192)
                if not b: break
                h.update(b)
    except Exception as e:
        return "ERR:"+str(e)
    return h.hexdigest()

def file_preview(path, size=1024):
    try:
        with open(path,'rb') as f:
            data = f.read(size)
            return data.decode('utf-8', errors='replace')
    except Exception as e:
        return f"<cannot read: {e}>"

def is_binary(path):
    try:
        with open(path,'rb') as f:
            chunk = f.read(8000)
            if b'\0' in chunk:
                return True
            # heuristics: many non-text bytes?
            text_chars = bytearray({7,8,9,10,12,13,27} | set(range(0x20,0x100)))
            nontext = sum(1 for c in chunk if c not in text_chars)
            return nontext > 0.30 * len(chunk)
    except:
        return True

def main():
    p = Path('.').resolve()
    parser = argparse.ArgumentParser()
    parser.add_argument('--out', default='report_dir')
    args = parser.parse_args()
    out = Path(args.out)
    if out.exists():
        import shutil
        shutil.rmtree(out)
    out.mkdir(parents=True, exist_ok=True)

    manifest = {
        "root": str(p),
        "generated_at": time.asctime(),
        "files": []
    }

    for root, dirs, files in os.walk(p):
        # skip .git and .github/actions runner caches maybe
        if '.git' in root.split(os.sep):
            continue
        for fn in files:
            path = Path(root) / fn
            rel = path.relative_to(p).as_posix()
            try:
                size = path.stat().st_size
            except:
                size = -1
            entry = {"path": rel, "size": size}
            ext = path.suffix.lower()
            entry['ext'] = ext
            entry['sha256'] = short_hash(path)
            entry['binary'] = is_binary(path)
            if size <= 200*1024 and not entry['binary']:
                entry['preview'] = file_preview(path, size=2000)
            else:
                entry['preview'] = None
            manifest['files'].append(entry)

    # quick summaries:
    ext_count = {}
    for e in manifest['files']:
        ext_count[e['ext']] = ext_count.get(e['ext'], 0) + 1
    manifest['summary'] = {
        "total_files": len(manifest['files']),
        "by_extension": sorted(ext_count.items(), key=lambda x: -x[1])[:50]
    }

    # save json
    with open(out/'scan.json','w',encoding='utf-8') as f:
        json.dump(manifest, f, ensure_ascii=False, indent=2)

    # write simple human report
    with open(out/'report.txt','w',encoding='utf-8') as f:
        f.write("Repository scan report\n")
        f.write("Root: %s\nGenerated: %s\n\n" % (manifest['root'], manifest['generated_at']))
        f.write("Total files (scanned): %d\n\n" % manifest['summary']['total_files'])
        f.write("Top extensions:\n")
        for ext, cnt in manifest['summary']['by_extension']:
            f.write("  %s : %d\n" % (ext or '(no ext)', cnt))
        f.write("\nSample files (first 200):\n")
        for i, e in enumerate(manifest['files'][:200]):
            f.write("%03d: %s  size=%s  sha256=%s  binary=%s\n" % (i+1, e['path'], e['size'], e['sha256'][:10], e['binary']))
        f.write("\n\nYou can find full JSON at scan.json\n")

    # create zip of report_dir for artifact
    zname = out.with_suffix('.zip')
    with zipfile.ZipFile(zname, 'w', zipfile.ZIP_DEFLATED) as z:
        z.write(out/'scan.json', arcname='scan.json')
        z.write(out/'report.txt', arcname='report.txt')

    print("Scan finished. Output:", out, "and", zname)

if __name__ == "__main__":
    main()
