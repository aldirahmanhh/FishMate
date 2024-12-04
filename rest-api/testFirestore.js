require('dotenv').config();
const db = require('./src/config/database'); // Import Firestore dari database.js

(async () => {
    try {
        const testCollection = db.collection('test');
        const snapshot = await testCollection.limit(1).get();
        console.log('Firestore terhubung. Koleksi "test" memiliki', snapshot.size, 'dokumen.');
    } catch (error) {
        console.error('Gagal terhubung ke Firestore:', error.message);
    }
})();
