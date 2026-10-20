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

# Starts SQL Server and runs create-database.sql only when the database does not exist yet (issue #27)
COPY docker/sqlserver-entrypoint.sh /usr/config/entrypoint.sh
RUN sed -i 's/\r$//' /usr/config/entrypoint.sh && chmod +x /usr/config/entrypoint.sh

EXPOSE 1433

CMD ["/usr/config/entrypoint.sh"]