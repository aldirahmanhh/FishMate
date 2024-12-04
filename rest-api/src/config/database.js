// const mysql = require('mysql2');
const Firestore = require('@google-cloud/firestore');

//Create connection to database
// const dbPool = mysql.createPool({
//     host: process.env.DB_HOST,
//     user: process.env.DB_USERNAME,
//     password: process.env.DB_PASSWORD,
//     database: process.env.DB_NAME
// });

// module.exports = dbPool.promise();


const db = new Firestore({
    projectId: process.env.FIRESTORE_PROJECT_ID,
    keyFilename: process.env.GOOGLE_APPLICATION_CREDENTIALS,
});

module.exports = db;