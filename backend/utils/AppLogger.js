/**
 * AppLogger — wrapper console.log yang hanya aktif di development.
 * Di production, log di-skip (kecuali error/warn).
 */
const DEBUG = process.env.NODE_ENV !== 'production';

const AppLogger = {
    d: (msg) => {
        if (DEBUG) console.log(msg);
    },
    i: (msg) => {
        if (DEBUG) console.info(msg);
    },
    w: (msg) => {
        // Warn tetap tampil (tapi bisa di-skip kalau perlu)
        console.warn(msg);
    },
    e: (msg) => {
        // Error selalu tampil
        console.error(msg);
    },
};

module.exports = AppLogger;
