import psycopg2
from psycopg2 import sql

# Define your PostgreSQL connection details
connection_params = {
    "dbname": "postgres",       # Your database name
    "user": "postgres",         # Your PostgreSQL username
    "password": "asd", # Your password
    "host": "localhost",         # Hostname (usually localhost)
    "port": "5432"               # Port (usually 5432 for PostgreSQL)
}

try:
    # Connect to PostgreSQL database
    connection = psycopg2.connect(**connection_params)
    
    # Create a cursor object
    cursor = connection.cursor()

    # Print connection success message
    print("Connected to the PostgreSQL database!")

    # Query to list tables in the 'public' schema
    cursor.execute(
        sql.SQL("""
            SELECT table_name
            FROM information_schema.tables
            WHERE table_schema = 'public'
            AND table_type = 'BASE TABLE';
        """)
    )

    # Fetch the list of tables
    tables = cursor.fetchall()

    # Print the list of tables
    print("Tables in the 'public' schema:")
    for table in tables:
        print(table[0])

    # Optional: Check if the 'houses' table exists
    if ('houses',) in tables:
        print("'houses' table is found in the database.")
    else:
        print("'houses' table is NOT found.")

except Exception as error:
    print(f"Error connecting to the PostgreSQL database: {error}")

finally:
    if connection:
        cursor.close()
        connection.close()
        print("PostgreSQL connection closed.")
