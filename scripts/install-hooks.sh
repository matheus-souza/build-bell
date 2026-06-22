#!/bin/sh
# Installs git hooks from scripts/hooks/ into .git/hooks/.
# Run once after cloning: sh scripts/install-hooks.sh
set -e
HOOKS_SRC="scripts/hooks"
HOOKS_DEST=".git/hooks"

if [ ! -d "$HOOKS_DEST" ]; then
  echo "Erro: diretório .git/hooks não encontrado. Execute na raiz do repositório."
  exit 1
fi

for hook in "$HOOKS_SRC"/*; do
  name=$(basename "$hook")
  cp "$hook" "$HOOKS_DEST/$name"
  chmod +x "$HOOKS_DEST/$name"
  echo "Hook instalado: $name"
done

echo "Todos os hooks instalados com sucesso."
