const authModel = require('../models/authModelFirestore');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

const register = async (req, res) => {
    try {
        const { username, email, password } = req.body;
        if (!username || !email || !password) {
            return res.status(400).json({
                message: 'Semua input harus terisi'
            })
        }

        const userExist = await authModel.findUserEmail(email);

        if (userExist) {
            return res.status(400).json({
                message: 'Email sudah melakukan register'
            })
        }

        const hashedPassword = await bcrypt.hash(password, 10);
        const createdAt = new Date().toISOString();
        const updatedAt = createdAt;

        await authModel.registerUser({ username, email, password: hashedPassword, createdAt, updatedAt });

        res.status(201).json({
            message: 'User baru berhasil dibuat',
            data: username
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
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({
                message: 'Email dan password harus diisi'
            })
        }

        const user = await authModel.findUserEmail(email);
        if(!user) {
            return res.status(400).json({
                message: 'Email atau password tidak valid'
            })
        }

        const isMatch = await bcrypt.compare(password, user.password);
        if(!isMatch) {
            return res.status(400).json({
                message: 'Email atau password tidak valid'
            })
        }

        const token = jwt.sign({userId: user.id}, 'secretkey', {expiresIn: '1h'});
        res.json({
            message: 'Login berhasil',
            token
        });


    } catch (error) {
        console.error('Error in login:', error); // Log error untuk debugging
        res.status(500).json({
            message: 'Server Error',
            serverMessage: error.message || error
        });
    }
}


module.exports = {
    register,
    login
}