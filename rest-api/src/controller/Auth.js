const authModel = require('../models/authModelFirestore');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const joi = require('joi');
const crypto = require('crypto');
const nodemailer = require('nodemailer');

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

const forgotPasswordRule = joi.object({
    email: joi.string().email().required()
});

const resetPasswordRule = joi.object({
    email: joi.string().email().required(),
    token: joi.string().required(),
    newPassword: joi.string().min(6).required(),
    confirmPassword: joi.string().min(6).required()
});

const register = async (req, res) => {
    try {
        const { username, email, password } = req.body;
        const emailInsensitive = email.toLowerCase();

        const { error } = registerRule.validate(req.body);
        if (error) {
            return res.status(400).json({
                message: 'Input tidak valid. Silahkan coba lagi',
                detail: error.details.map(err => err.message)
            });
        };

        if (!username || !emailInsensitive || !password) {
            return res.status(400).json({
                message: 'Semua input harus terisi'
            });
        };

        const userExist = await authModel.findUserEmail(emailInsensitive);

        if (userExist) {
            return res.status(400).json({
                error: true,
                message: 'Email sudah melakukan register'
            });
        };

        const userId = crypto.randomUUID();
        const hashedPassword = await bcrypt.hash(password, 10);
        const createdAt = new Date().toISOString();
        const updatedAt = createdAt;

        await authModel.registerUser({ userId, username, email: emailInsensitive, password: hashedPassword, createdAt, updatedAt });

        res.status(201).json({
            error: false,
            message: 'User baru berhasil dibuat',
            data: { username }
        });

    } catch (error) {
        console.error('Error in register:', error); // Log error untuk debugging
        res.status(500).json({
            error: true,
            message: 'Server Error',
            serverMessage: error.message || error
        });
    };
};

const login = async (req, res) => {
    try {
        const { email, password } = req.body;

        const emailInsensitive = email.toLowerCase();

        const { error } = loginRule.validate(req.body);
        if (error) {
            return res.status(400).json({
                message: 'Input tidak valid. Silahkan coba lagi',
                detail: error.details.map(err => err.message)
            });
        };


        if (!emailInsensitive || !password) {
            return res.status(400).json({
                error: true,
                message: 'Email dan password harus diisi'
            });
        };

        const user = await authModel.findUserEmail(emailInsensitive);
        if (!user) {
            return res.status(400).json({
                error: true,
                message: 'Email atau password tidak valid'
            });
        };

        const isMatch = await bcrypt.compare(password, user.password);

        if (!isMatch) {
            return res.status(400).json({
                error: true,
                message: 'Email atau password tidak valid'
            });
        };

        const token = jwt.sign({ userId: user.userId, name: user.username }, 'secretkey', { expiresIn: '365d' });
        res.json({
            error: false,
            message: 'Login berhasil',
            loginResult: {
                id: user.userId,
                name: user.username,
                token
            }
        });


    } catch (error) {
        console.error('Error in login:', error); // Log error untuk debugging
        res.status(500).json({
            error: true,
            message: 'Server Error',
            serverMessage: error.message || error
        });
    };
};

const changePassword = async (req, res) => {
    try {
        const { email, password, newPassword, confirmPassword } = req.body;
        const emailInsensitive = email.toLowerCase();

        const { error } = changePasswordRule.validate(req.body);
        if (error) {
            return res.status(400).json({
                error: true,
                message: 'Input tidak valid. Silahkan coba lagi',
                detail: error.details.map(err => err.message)
            });
        };

        if (!emailInsensitive || !password || !newPassword || !confirmPassword) {
            return res.status(400).json({
                error: true,
                message: 'Semua input harus terisi'
            });
        };

        if (newPassword !== confirmPassword) {
            return res.status(400).json({
                error: true,
                message: 'Password tidak cocok'
            });
        };

        const user = await authModel.findUserEmail(emailInsensitive);
        if (!user) {
            return res.status(400).json({
                error: true,
                message: 'Email atau password tidak valid'
            });
        };

        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({
                error: true,
                message: 'Email atau password tidak valid'
            });
        };

        const hashedPassword = await bcrypt.hash(newPassword, 10);
        const updatedAt = new Date().toISOString();

        await authModel.updatePassword(emailInsensitive, hashedPassword, updatedAt);

        res.status(201).json({
            error: false,
            message: 'Password berhasil diperbarui'
        });
    } catch (error) {
        console.error('Error in changePassword:', error); // Log error untuk debugging
        res.status(500).json({
            error: true,
            message: 'Server Error',
            serverMessage: error.message || error
        });
    };
};

const changeUsername = async (req, res) => {
    try {
        const { email, newUsername } = req.body;
        const emailInsensitive = email.toLowerCase();

        const { error } = changeUsernameRule.validate(req.body);
        if (error) {
            return res.status(400).json({
                error: true,
                message: 'Input tidak valid. Silahkan coba lagi',
                detail: error.details.map(err => err.message)
            });
        };

        if (!emailInsensitive || !newUsername) {
            res.status(400).json({
                error: true,
                message: 'Semua input harus terisi'
            });
        };

        const user = await authModel.findUserEmail(emailInsensitive);
        if (!user) {
            return res.status(400).json({
                error: true,
                message: 'Email tidak valid'
            });
        };

        const updatedAt = new Date().toISOString();

        await authModel.updateUsername(emailInsensitive, newUsername, updatedAt);
        res.status(201).json({
            error: false,
            message: 'Username berhasil diperbarui',
            name: newUsername
        });

    } catch (error) {
        console.error('Error in changeUsername:', error); // Log error untuk debugging
        res.status(500).json({
            error: true,
            message: 'Server Error',
            serverMessage: error.message || error
        });
    };
};

const forgotPassword = async (req, res) => {
    try {
        const { email } = req.body;
        const emailInsensitive = email.toLowerCase();

        const { error } = forgotPasswordRule.validate(req.body);
        if (error) {
            return res.status(400).json({
                message: 'Input tidak valid. Silahkan coba lagi',
                detail: error.details.map(err => err.message)
            });
        };

        const user = await authModel.findUserEmail(emailInsensitive);
        if (!user) {
            return res.status(400).json({
                error: true,
                message: 'Email tidak valid'
            });
        };

        const token = crypto.randomBytes(3).toString('hex');
        const expiresIn = Date.now() + 20 * 60 * 1000;

        await authModel.resetToken(emailInsensitive, token, expiresIn);

        // const resetLink = `http://localhost:4000/resetPassword?token=${token}&email=${emailInsensitive}`;
        console.log(`Send this link to user's email: ${token}`);

        const transporter = nodemailer.createTransport({
            host: 'smtp.gmail.com',
            port: 587,
            secure: false, // gunakan false untuk port 587
            auth: {
                user: process.env.EMAIL_SENDER,
                pass: process.env.EMAIL_PASS
            }
        });

        const mailOption = {
            from: process.env.EMAIL_SENDER,
            to: emailInsensitive,
            subject: 'Fish Mate Reset Password',
            text: `Gunakan token berikut untuk reset password: ${token}`
        };

        transporter.sendMail(mailOption, (error, info) => {
            if (error) {
                console.error('Error sending email: ', error);
                res.status(500).json({
                    error: true,
                    message: 'Server Error',
                    serverMessage: error.message || error
                });
            } else {
                console.log('Email sent' + info.response);
                res.status(201).json({
                    error: false,
                    message: 'Periksa email anda untuk reset password',
                });
            };
        });

    } catch (error) {
        console.error('Error in forgotPassword:', error); // Log error untuk debugging
        res.status(500).json({
            error: true,
            message: 'Server Error',
            serverMessage: error.message || error
        });
    };
};

const resetPassword = async (req, res) => {
    try {
        const { email, token, newPassword, confirmPassword } = req.body;
        const emailInsensitive = email.toLowerCase();

        const { error } = resetPasswordRule.validate(req.body);
        if (error) {
            return res.status(400).json({
                message: 'Input tidak valid. Silahkan coba lagi',
                detail: error.details.map(err => err.message)
            });
        };

        if (!emailInsensitive || !token || !newPassword || !confirmPassword) {
            return res.status(400).json({
                error: true,
                message: 'Semua input harus terisi'
            });
        };

        if (newPassword !== confirmPassword) {
            return res.status(400).json({
                error: true,
                message: 'Password tidak cocok'
            });
        };

        const resetData = await authModel.findToken(emailInsensitive, token);
        if (!resetData || resetData.expiresIn < Date.now()) {
            return res.status(400).json({
                error: true,
                message: 'Token tidak valid atau telah kedaluwarsa'
            });
        };

        const hashedPassword = await bcrypt.hash(newPassword, 10);
        const updatedAt = new Date().toISOString();

        await authModel.updatePassword(emailInsensitive, hashedPassword, updatedAt);
        await authModel.deleteToken(emailInsensitive, token);

        res.status(201).json({
            error: false,
            message: 'Password berhasil diperbarui'
        });

    } catch (error) {
        console.error('Error in resetPassword:', error); // Log error untuk debugging
        res.status(500).json({
            error: true,
            message: 'Server Error',
            serverMessage: error.message || error
        });
    }
}

module.exports = {
    register,
    login,
    changePassword,
    changeUsername,
    forgotPassword,
    resetPassword
};