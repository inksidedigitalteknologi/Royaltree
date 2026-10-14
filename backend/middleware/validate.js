/**
 * Validation Middleware
 * Royaltree — pakai Joi
 */

const Joi = require('joi');

/**
 * Factory: bikin middleware validate dari schema Joi
 * @param {Joi.Schema} schema
 * @param {string} source - 'body' | 'query' | 'params' (default body)
 */
const validate = (schema, source = 'body') => (req, res, next) => {
    const { error, value } = schema.validate(req[source], {
        abortEarly: false,      // kumpulkan semua error
        stripUnknown: true,     // buang field yang tidak dikenal
        convert: true           // auto-convert tipe (string "123" → 123)
    });

    if (error) {
        const errors = error.details.map(d => ({
            field: d.path.join('.'),
            message: d.message.replace(/"/g, '')
        }));
        return res.status(400).json({
            success: false,
            message: 'Validasi gagal',
            errors
        });
    }

    // Replace req[source] dengan value yang sudah tervalidasi (bersih)
    req[source] = value;
    next();
};

module.exports = { validate, Joi };
