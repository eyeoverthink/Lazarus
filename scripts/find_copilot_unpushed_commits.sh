#!/usr/bin/env bash
# Quickly list local copilot/* branches that are ahead of origin (un-pushed commits).
set -euo pipefail

cd "$(dirname "$0")/.."

git fetch --all --prune >/dev/null

branches=$(git for-each-ref 'refs/heads/copilot/*' --format='%(refname:short)')

if [[ -z "${branches}" ]]; then
  echo "No local copilot/* branches found."
  exit 0
fi

found_unpushed=0

for branch in ${branches}; do
  remote="origin/${branch}"
  if git show-ref --verify --quiet "refs/remotes/${remote}"; then
    read -r ahead behind <<<"$(git rev-list --left-right --count "${remote}...${branch}")"
    if [[ "${ahead}" -gt 0 ]]; then
      found_unpushed=1
      echo "⚠️  ${branch} is ${ahead} commit(s) ahead of ${remote}:"
      git --no-pager log --oneline "${remote}..${branch}"
      echo
    fi
  else
    found_unpushed=1
    echo "⚠️  ${branch} has no remote tracking branch (${remote} missing)."
    git --no-pager log --oneline "${branch}"
    echo
  fi
done

if [[ "${found_unpushed}" -eq 0 ]]; then
  echo "All local copilot/* branches are pushed to origin."
fi
