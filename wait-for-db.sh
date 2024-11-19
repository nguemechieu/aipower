# wait-for-db.sh
#!/bin/sh
set -e
host="$1"
shift
until mysqladmin ping -h "$host" --silent; do
  echo "Waiting for MySQL..."
  sleep 1
done
exec "$@"
