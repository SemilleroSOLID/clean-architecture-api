FROM mcr.microsoft.com/mssql/server:2022-latest

USER root

# Install SQL Server tools
RUN apt-get update
RUN apt-get install -y curl gnupg2
RUN curl https://packages.microsoft.com/keys/microsoft.asc | apt-key add -
RUN curl https://packages.microsoft.com/config/ubuntu/20.04/prod.list > /etc/apt/sources.list.d/mssql-release.list
RUN apt-get update
RUN DEBIAN_FRONTEND=noninteractive apt-get install -f
RUN ACCEPT_EULA=Y DEBIAN_FRONTEND=noninteractive apt-get install -y mssql-tools
RUN apt-get clean
RUN rm -rf /var/lib/apt/lists/*

# Add SQL Server tools to path
ENV PATH="/opt/mssql-tools/bin:${PATH}"

# Create work directory
WORKDIR /usr/src/app

# Copy the SQL script
COPY create-database.sql ./

# Set environment variables
ENV ACCEPT_EULA=Y
# SA_PASSWORD is the password for the SA user. It must be set in the Dockerfile. It must be with at least 8 characters, 1 number and 1 special character.
ENV SA_PASSWORD=your_password_123_.

# Create entrypoint script
RUN export SA_PASSWORD=${SA_PASSWORD} && \
    mkdir -p /usr/config && \
    echo "#!/bin/bash" > /usr/config/entrypoint.sh && \
    echo "/opt/mssql/bin/sqlservr & sleep 30" >> /usr/config/entrypoint.sh && \
    echo "# Wait for SQL Server to be ready" >> /usr/config/entrypoint.sh && \
    echo "for i in {1..50};" >> /usr/config/entrypoint.sh && \
    echo "do" >> /usr/config/entrypoint.sh && \
    echo "    /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -Q 'SELECT 1' &> /dev/null" >> /usr/config/entrypoint.sh && \
    echo "    if [ \$? -eq 0 ]" >> /usr/config/entrypoint.sh && \
    echo "    then" >> /usr/config/entrypoint.sh && \
    echo "        echo 'SQL Server is ready'" >> /usr/config/entrypoint.sh && \
    echo "        break" >> /usr/config/entrypoint.sh && \
    echo "    else" >> /usr/config/entrypoint.sh && \
    echo "        echo 'Waiting for SQL Server...'" >> /usr/config/entrypoint.sh && \
    echo "        sleep 1" >> /usr/config/entrypoint.sh && \
    echo "    fi" >> /usr/config/entrypoint.sh && \
    echo "done" >> /usr/config/entrypoint.sh && \
    echo "" >> /usr/config/entrypoint.sh && \
    echo "echo 'Creating database and tables...'" >> /usr/config/entrypoint.sh && \
    echo "/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -d master -i create-database.sql" >> /usr/config/entrypoint.sh && \
    echo "" >> /usr/config/entrypoint.sh && \
    echo "echo 'Showing Requirements table:'" >> /usr/config/entrypoint.sh && \
    echo "/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -d CleanArchitectureDB -Q 'SELECT * FROM Requirement;'" >> /usr/config/entrypoint.sh && \
    echo "" >> /usr/config/entrypoint.sh && \
    echo "echo 'Showing ConvocationType table:'" >> /usr/config/entrypoint.sh && \
    echo "/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -d CleanArchitectureDB -Q 'SELECT * FROM ConvocationType;'" >> /usr/config/entrypoint.sh && \
    echo "" >> /usr/config/entrypoint.sh && \
    echo "tail -f /dev/null" >> /usr/config/entrypoint.sh && \
    chmod +x /usr/config/entrypoint.sh

EXPOSE 1433

CMD /usr/config/entrypoint.sh