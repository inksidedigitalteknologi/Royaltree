// utils/logger.js
// Structured logging dengan Winston + daily rotation

const winston = require('winston');
const DailyRotateFile = require('winston-daily-rotate-file');
const path = require('path');

const logDir = path.join(__dirname, '../../logs');
const isProd = process.env.NODE_ENV === 'production';

// Format untuk file (plain text dengan timestamp)
const fileFormat = winston.format.combine(
    winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss.SSS' }),
    winston.format.errors({ stack: true }),
    winston.format.printf(({ timestamp, level, message, stack, ...meta }) => {
        const metaStr = Object.keys(meta).length
            ? ` ${JSON.stringify(meta)}`
            : '';
        const stackStr = stack ? `\n${stack}` : '';
        return `[${timestamp}] ${level.toUpperCase()}: ${message}${metaStr}${stackStr}`;
    })
);

// Format untuk console (colorize)
const consoleFormat = winston.format.combine(
    winston.format.timestamp({ format: 'HH:mm:ss' }),
    winston.format.errors({ stack: true }),
    winston.format.colorize(),
    winston.format.printf(({ timestamp, level, message, stack, ...meta }) => {
        const metaStr = Object.keys(meta).length
            ? ` ${JSON.stringify(meta)}`
            : '';
        const stackStr = stack ? `\n${stack}` : '';
        return `[${timestamp}] ${level}: ${message}${metaStr}${stackStr}`;
    })
);

const logger = winston.createLogger({
    level: process.env.LOG_LEVEL || (isProd ? 'info' : 'debug'),
    format: fileFormat,
    transports: [
        new winston.transports.Console({ format: consoleFormat }),
        new DailyRotateFile({
            filename: path.join(logDir, 'app-%DATE%.log'),
            datePattern: 'YYYY-MM-DD',
            maxFiles: '14d',
            maxSize: '20m',
            format: fileFormat
        }),
        new DailyRotateFile({
            filename: path.join(logDir, 'error-%DATE%.log'),
            datePattern: 'YYYY-MM-DD',
            level: 'error',
            maxFiles: '30d',
            maxSize: '20m',
            format: fileFormat
        })
    ],
    exceptionHandlers: [
        new DailyRotateFile({
            filename: path.join(logDir, 'exceptions-%DATE%.log'),
            datePattern: 'YYYY-MM-DD',
            maxFiles: '30d',
            format: fileFormat
        })
    ],
    rejectionHandlers: [
        new DailyRotateFile({
            filename: path.join(logDir, 'rejections-%DATE%.log'),
            datePattern: 'YYYY-MM-DD',
            maxFiles: '30d',
            format: fileFormat
        })
    ],
    exitOnError: false
});

// Helper: log dengan context
logger.withContext = (context) => {
    return {
        info: (msg, meta) => logger.info(msg, { ...meta, context }),
        warn: (msg, meta) => logger.warn(msg, { ...meta, context }),
        error: (msg, meta) => logger.error(msg, { ...meta, context }),
        debug: (msg, meta) => logger.debug(msg, { ...meta, context })
    };
};

module.exports = logger;
