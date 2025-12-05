const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');
require('dotenv').config();

const dbConfig = {
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'kostlin',
    port: process.env.DB_PORT || 3306,
    multipleStatements: true
};

async function migrate() {
    let connection;
    
    try {
        console.log('🔄 Connecting to database...');
        connection = await mysql.createConnection(dbConfig);
        console.log('✅ Connected to database:', dbConfig.database);

        // Read schema file
        const schemaPath = path.join(__dirname, 'schema.sql');
        const schemaSQL = fs.readFileSync(schemaPath, 'utf8');

        // Remove CREATE DATABASE and USE statements since database already exists
        let cleanedSQL = schemaSQL
            .replace(/CREATE DATABASE IF NOT EXISTS kostlin[^;]*;/gi, '')
            .replace(/USE kostlin;/gi, '')
            .trim();

        // Split by semicolon, but handle multi-line statements properly
        const statements = [];
        let currentStatement = '';
        const lines = cleanedSQL.split('\n');
        
        for (const line of lines) {
            const trimmedLine = line.trim();
            
            // Skip empty lines and full-line comments
            if (!trimmedLine || trimmedLine.startsWith('--')) {
                continue;
            }
            
            // Remove inline comments
            const lineWithoutComment = trimmedLine.split('--')[0].trim();
            if (!lineWithoutComment) continue;
            
            currentStatement += lineWithoutComment + ' ';
            
            // If line ends with semicolon, we have a complete statement
            if (trimmedLine.endsWith(';')) {
                const stmt = currentStatement.trim();
                if (stmt && stmt.length > 1) { // More than just semicolon
                    statements.push(stmt);
                }
                currentStatement = '';
            }
        }
        
        // Add any remaining statement
        if (currentStatement.trim()) {
            statements.push(currentStatement.trim());
        }

        console.log(`📝 Found ${statements.length} SQL statements to execute...\n`);

        // Execute each statement
        for (let i = 0; i < statements.length; i++) {
            const statement = statements[i];
            
            // Skip if empty or just whitespace
            if (!statement || statement.trim().length === 0) continue;

            try {
                await connection.query(statement + ';');
                
                // Extract table name for logging
                const tableMatch = statement.match(/CREATE TABLE.*?IF NOT EXISTS.*?`?(\w+)`?/i) || 
                                  statement.match(/CREATE TABLE.*?`?(\w+)`?/i);
                const tableName = tableMatch ? tableMatch[1] : null;
                
                if (statement.toUpperCase().includes('CREATE TABLE')) {
                    console.log(`✅ Created table: ${tableName || 'unknown'}`);
                } else if (statement.toUpperCase().includes('INSERT INTO')) {
                    const insertMatch = statement.match(/INSERT INTO.*?`?(\w+)`?/i);
                    const insertTable = insertMatch ? insertMatch[1] : 'table';
                    const valueMatch = statement.match(/VALUES\s*\(/gi);
                    const rowCount = valueMatch ? statement.split(/VALUES\s*\(/gi).length - 1 : 1;
                    console.log(`✅ Inserted ${rowCount} row(s) into: ${insertTable}`);
                }
            } catch (error) {
                // Ignore "table already exists" errors
                if (error.code === 'ER_TABLE_EXISTS_ERROR' || error.message.includes('already exists')) {
                    const tableMatch = statement.match(/CREATE TABLE.*?IF NOT EXISTS.*?`?(\w+)`?/i) ||
                                      statement.match(/CREATE TABLE.*?`?(\w+)`?/i);
                    const tableName = tableMatch ? tableMatch[1] : 'table';
                    console.log(`⚠️  Table already exists: ${tableName} (skipped)`);
                } else if (error.code === 'ER_DUP_ENTRY') {
                    console.log(`⚠️  Duplicate entry (skipped)`);
                } else {
                    console.error(`❌ Error executing statement ${i + 1}:`, error.message);
                    console.error('Statement preview:', statement.substring(0, 150) + '...');
                }
            }
        }

        console.log('\n✨ Migration completed successfully!');
        
    } catch (error) {
        console.error('❌ Migration failed:', error.message);
        process.exit(1);
    } finally {
        if (connection) {
            await connection.end();
            console.log('🔌 Database connection closed');
        }
    }
}

// Run migration
migrate();

