const authModel = require('../models/authModelFirestore');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const joi = require('joi');
const crypto = require('crypto');

const registerRule = joi.object({
    username: joi.string().min(3).max(30).required(),
    email: joi.string().email().required(),
    password: joi.string().min(6).required()
});

const loginRule = joi.object({
    email: joi.string().email().required(),
    password: joi.string().min(6).required()
});

const changePasswordRule = joi.object({
    email: joi.string().email().required(),
    password: joi.string().min(6).required(),
    newPassword: joi.string().min(6).required(),
    confirmPassword: joi.string().min(6).required()
});

const changeUsernameRule = joi.object({
    email: joi.string().email().required(),
    newUsername: joi.string().min(3).max(30).required()
});

const register = async (req, res) => {
    try {
        const { error } = registerRule.validate(req.body);
        if (error) {
            return res.status(400).json({
                message: 'Input tidak valid. Silahkan coba lagi',
                detail: error.details.map(err => err.message)
            });
        };

        const { username, email, password } = req.body;
        if (!username || !email || !password) {
            return res.status(400).json({
                message: 'Semua input harus terisi'
            });
        };

        const userExist = await authModel.findUserEmail(email);

        if (userExist) {
            return res.status(400).json({
                message: 'Email sudah melakukan register'
            });
        };

        const userId = crypto.randomUUID();
        const hashedPassword = await bcrypt.hash(password, 10);
        const createdAt = new Date().toISOString();
        const updatedAt = createdAt;

        await authModel.registerUser({ userId, username, email, password: hashedPassword, createdAt, updatedAt });

        res.status(201).json({
            message: 'User baru berhasil dibuat',
            data: { username, userId }
        });

    } catch (error) {
        console.error('Error in register:', error); // Log error untuk debugging
        res.status(500).json({
            message: 'Server Error',
            serverMessage: error.message || error
        });
    };
};

const login = async (req, res) => {
    try {
        const { error } = loginRule.validate(req.body);
        if (error) {
            return res.status(400).json({
                message: 'Input tidak valid. Silahkan coba lagi',
                detail: error.details.map(err => err.message)
            });
        };

        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({
                message: 'Email dan password harus diisi'
            });
        };

        const user = await authModel.findUserEmail(email);
        if (!user) {
            return res.status(400).json({
                message: 'Email atau password tidak valid'
            });
        };

        const isMatch = await bcrypt.compare(password, user.password);

        if (!isMatch) {
            return res.status(400).json({
                message: 'Email atau password tidak valid'
            });
        };

        const token = jwt.sign({ userId: user.id, name: user.username }, 'secretkey', { expiresIn: '1h' });
        res.json({
            message: 'Login berhasil',
            name: user.username,
            token
        });


    } catch (error) {
        console.error('Error in login:', error); // Log error untuk debugging
        res.status(500).json({
            message: 'Server Error',
            serverMessage: error.message || error
        });
    };
};

const changePassword = async (req, res) => {
    try {
        const { error } = changePasswordRule.validate(req.body);
        if (error) {
            return res.status(400).json({
                message: 'Input tidak valid. Silahkan coba lagi',
                detail: error.details.map(err => err.message)
            });
        };

        const { email, password, newPassword, confirmPassword } = req.body;
        if (!email || !password || !newPassword || !confirmPassword) {
            return res.status(400).json({
                message: 'Semua input harus terisi'
            });
        };

        if (newPassword !== confirmPassword) {
            return res.status(400).json({
                message: 'Password tidak cocok'
            });
        };

        const user = await authModel.findUserEmail(email);
        if (!user) {
            return res.status(400).json({
                message: 'Email atau password tidak valid'
            });
        };

        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({
                message: 'Email atau password tidak valid'
            });
        };

        const hashedPassword = await bcrypt.hash(newPassword, 10);
        const updatedAt = new Date().toISOString();

        await authModel.updatePassword(email, hashedPassword, updatedAt);

        res.status(201).json({
            message: 'Password berhasil diperbarui'
        });
    } catch (error) {
        console.error('Error in changePassword:', error); // Log error untuk debugging
        res.status(500).json({
            message: 'Server Error',
            serverMessage: error.message || error
        });
    };
};

const changeUsername = async (req, res) => {
    try {
        const { error } = changeUsernameRule.validate(req.body);
        if (error) {
            return res.status(400).json({
                message: 'Input tidak valid. Silahkan coba lagi',
                detail: error.details.map(err => err.message)
            });
        };

        const { email, newUsername } = req.body;
        if (!email || !newUsername) {
            res.status(400).json({
                message: 'Semua input harus terisi'
            });
        };

        const user = await authModel.findUserEmail(email);
        if (!user) {
            return res.status(400).json({
                message: 'Email atau password tidak valid'
            });
        };

        const updatedAt = new Date().toISOString();

        await authModel.updateUsername(email, newUsername, updatedAt);
        res.status(201).json({
            message: 'Username berhasil diperbarui',
            name: newUsername
        });

    } catch (error) {
        console.error('Error in changeUsername:', error); // Log error untuk debugging
        res.status(500).json({
            message: 'Server Error',
            serverMessage: error.message || error
        });
    };
};




module.exports = {
    register,
    login,
    changePassword,
    changeUsername
};