#!/bin/sh
# Génère docs/rapport/rapport.pdf : diagrammes SVG de docs/uml convertis en PDF, puis LuaLaTeX via latexmk.
# Prérequis : latexmk, LuaLaTeX, rsvg-convert (voir LISEZMOI.md).
set -e
cd "$(dirname "$0")"
mkdir -p build/figures
for svg in ../../uml/*.svg; do
  rsvg-convert -f pdf -o "build/figures/$(basename "$svg" .svg).pdf" "$svg"
done
latexmk -lualatex -interaction=nonstopmode -halt-on-error -outdir=build rapport.tex
cp build/rapport.pdf ../rapport.pdf
echo "PDF généré : docs/rapport/rapport.pdf"
