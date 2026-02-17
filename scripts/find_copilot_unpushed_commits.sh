#!/usr/bin/env bash
# Quickly list local copilot/* branches that are ahead of origin (un-pushed commits).
set -euo pipefail

cd "$(dirname "$0")/.."

git fetch --all --prune >/dev/null

mapfile -t branches < <(git for-each-ref 'refs/heads/copilot/*' --format='%(refname:short)')

if [[ ${#branches[@]} -eq 0 ]]; then
  echo "No local copilot/* branches found."
  exit 0
fi

found_unpushed=0

for branch in "${branches[@]}"; do
  remote="origin/${branch}"
  if git show-ref --verify --quiet "refs/remotes/${remote}"; then
    read -r ahead behind <<<"$(git rev-list --left-right --count "${remote}...${branch}" || echo "0 0")"
    ahead=${ahead:-0}
    behind=${behind:-0}
    if [[ "${ahead}" -gt 0 ]]; then
      found_unpushed=1
      echo "⚠️  ${branch} is ${ahead} commit(s) ahead of ${remote}:"
      git --no-pager log -10 --oneline "${remote}..${branch}"
      echo
    elif [[ "${behind}" -gt 0 ]]; then
      echo "ℹ️  ${branch} is ${behind} commit(s) behind ${remote} (no unpushed commits)."
    fi
  else
    found_unpushed=1
    echo "⚠️  ${branch} has no remote tracking branch (${remote} missing)."
    git --no-pager log -10 --oneline "${branch}"
    echo
  fi
done

if [[ "${found_unpushed}" -eq 0 ]]; then
  echo "All local copilot/* branches are pushed to origin."
fi
