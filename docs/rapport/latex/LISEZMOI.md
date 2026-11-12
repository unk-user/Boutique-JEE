# Rapport PDF (LaTeX)

Sources du rapport remis à l'encadrante. Le contenu reprend `docs/rapport/rapport.md`.

## Prérequis (Fedora)

```sh
sudo dnf install latexmk texlive-luatex texlive-luahbtex texlive-babel-french texlive-hyphen-french \
  texlive-lm texlive-lm-math librsvg2-tools 'tex(fontspec.sty)' 'tex(xltabular.sty)' 'tex(tcolorbox.sty)' \
  'tex(titlesec.sty)' 'tex(enumitem.sty)' 'tex(pdflscape.sty)' 'tex(newunicodechar.sty)'
```

## Compilation

```sh
./compiler.sh
```

Les diagrammes `docs/uml/*.svg` sont convertis en PDF dans `build/figures/`, puis LuaLaTeX produit
`docs/rapport/rapport.pdf`.

## À compléter

- `sections/07-difficultes.tex` : difficultés rencontrées et solutions.
- `captures/` : captures d'écran PNG, nommées comme dans `sections/09-annexes.tex` (un encadré rouge signale
  chaque capture manquante).
- `images/` : logos facultatifs de la page de garde (`logo-uiz.png`, `logo-ensa.png`).
