#!/bin/bash
# Starts SQL Server and creates CleanArchitectureDB only the first time (issue #27): with the data in a volume,
# running create-database.sql again on every start would duplicate the seed rows.
/opt/mssql/bin/sqlservr &

for i in {1..90}; do
    if /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -Q "SELECT 1" &> /dev/null; then
        echo "SQL Server is ready"
        break
    fi
    echo "Waiting for SQL Server..."
    sleep 1
done

EXISTS=$(/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -h -1 -W -Q "SET NOCOUNT ON; SELECT COUNT(*) FROM sys.databases WHERE name = 'CleanArchitectureDB'")
if [ "$EXISTS" = "0" ]; then
    echo "Creating database, tables and seed data..."
    /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -d master -b -i /usr/src/app/create-database.sql
else
    echo "CleanArchitectureDB already exists: keeping its data"
fi

# Keep the container alive as long as SQL Server runs
wait
