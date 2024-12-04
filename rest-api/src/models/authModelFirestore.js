const db = require('../config/database');

const findUserEmail = async (email) => {
    const userSnapshot = await db.collection('auth').where('email', '==', email).get();
    if(userSnapshot.empty) {
        return null;
    }
    return userSnapshot.docs[0].data();
}

const registerUser = async (body) => {
    const newUser = {
        username: body.username,
        email: body.email,
        password: body.password,
        createdAt: body.createdAt,
        updatedAt: body.updatedAt
    }
    await db.collection('auth').add(newUser);
}

module.exports = {
    findUserEmail,
    registerUser
};