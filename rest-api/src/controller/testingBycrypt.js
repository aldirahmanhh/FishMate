// bcryptTest.js
const bcrypt = require('bcryptjs');

const hashedPassword = "$2a$10$zw0AkaZD9hom1/YeUv3eVeDwHVV/Os82QKllp3IXGMfxB9IHn38vK"; // Hash dari Firestore
const plaintextPassword = "098765";

bcrypt.compare(plaintextPassword, hashedPassword, (err, result) => {
    if (err) {
        console.error('Error:', err);
    } else {
        console.log('Hasil bcrypt.compare:', result); // Harus true
    }
});
