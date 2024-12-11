const db = require('../config/database');

const findUserEmail = async (email) => {
    const userSnapshot = await db.collection('auth').where('email', '==', email.toLowerCase()).get();
    if(userSnapshot.empty) {
        return null;
    };
    const userDoc = userSnapshot.docs[0];
    return { id: userDoc.id, ...userDoc.data() };
}

const registerUser = async (body) => {
    const newUser = {
        userId: body.userId,
        username: body.username,
        email: body.email,
        password: body.password,
        createdAt: body.createdAt,
        updatedAt: body.updatedAt
    };
    await db.collection('auth').add(newUser);
}

const updatePassword = async (email, newPassword, updatedAt) => {
    const userSnapshot = await db.collection('auth').where('email', '==', email).get();
    if (userSnapshot.empty) {
        throw new Error('User tidak ditemukan');
    };
    

    const userDoc = userSnapshot.docs[0];
    await userDoc.ref.update({
        password: newPassword,
        updatedAt: updatedAt
    });
};

const updateUsername = async (email, newUsername, updatedAt) => {
    const userSnapshot = await db.collection('auth').where('email', '==', email).get();
    if (userSnapshot.empty) {
        throw new Error('User tidak ditemukan');
    };

    const userDoc = userSnapshot.docs[0];
    await userDoc.ref.update({
        username: newUsername,
        updatedAt: updatedAt
    });

};

const resetToken = async (email, token, expiresIn) => {
    const tokenExist = await db.collection('resetToken').doc(email).get();
    if ( tokenExist.exists && tokenExist.data().expiresIn > Date.now()) {
        throw new Error('Token reset masih berlaku');
    }; 

    const resetData = {token, expiresIn};
    await db.collection('resetToken').doc(email).set(resetData);
}

const findToken = async (email, token) => {
    const doc = await db.collection('resetToken').doc(email).get();
    if (!doc.exists) return null;
    const data = doc.data();
    return data.token === token ? data : null;
}

const deleteToken = async (email) => {
    await db.collection('resetToken').doc(email).delete();
}

module.exports = {
    findUserEmail,
    registerUser,
    updatePassword,
    updateUsername,
    resetToken,
    findToken,
    deleteToken
};