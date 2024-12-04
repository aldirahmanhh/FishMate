const dbPool = require('../config/database');

const findUserEmail = async (email) => {
    const Query = `SELECT * FROM auth WHERE email = ?`;
    const [rows] = await dbPool.execute(Query, [email]);
    return rows.length > 0 ? rows[0] : null;
};

const registerUser = async (body) => {
    const Query = `INSERT INTO auth (username, email, password, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?)`;
    await dbPool.execute(Query, [body.username, body.email, body.password, body.createdAt, body.updatedAt]);
};

module.exports = {
    findUserEmail,
    registerUser
}

