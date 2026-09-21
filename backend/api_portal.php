<?php
/**
 * Royaltree Portal Backend API (Single File PHP)
 * -------------------------------------------------------------
 * Salin file ini ke folder web server Anda (misal: public_html/api/index.php).
 * Cocok untuk hosting cPanel, Apache, Nginx, maupun VPS PHP.
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

$API_SECRET_KEY = "rt_secret_portal_key_2026"; // Sesuaikan dengan Secret Key Anda

function checkAuth($secret) {
    $headers = getallheaders();
    $auth = isset($headers['Authorization']) ? $headers['Authorization'] : '';
    if (strpos($auth, 'Bearer ') === 0) {
        $token = substr($auth, 7);
        if ($token === $secret) return true;
    }
    http_response_code(403);
    echo json_encode(["success" => false, "message" => "Otorisasi API Key tidak valid."]);
    exit();
}

$requestUri = $_SERVER['REQUEST_URI'];
$method = $_SERVER['REQUEST_METHOD'];
$body = json_decode(file_get_contents('php://input'), true);

// Routing Sederhana
if (strpos($requestUri, '/health') !== false) {
    echo json_encode([
        "success" => true,
        "status" => "ONLINE",
        "message" => "Portal Royaltree PHP API Aktif & Siap Terhubung.",
        "timestamp" => round(microtime(true) * 1000)
    ]);
    exit();
}

if (strpos($requestUri, '/config') !== false) {
    if ($method === 'GET') {
        echo json_encode([
            "success" => true,
            "data" => [
                "maintenanceMode" => false,
                "minWithdrawalEWallet" => 50000,
                "minWithdrawalBank" => 100000,
                "freeTierCommissionRate" => 12.0,
                "vipTierCommissionRate" => 30.0,
                "serverVersion" => "1.4.0-php"
            ]
        ]);
    } else if ($method === 'PUT') {
        checkAuth($API_SECRET_KEY);
        echo json_encode(["success" => true, "message" => "Konfigurasi diperbarui."]);
    }
    exit();
}

if (strpos($requestUri, '/users/sync') !== false && $method === 'POST') {
    checkAuth($API_SECRET_KEY);
    // Simpan data $body ke MySQL Anda di sini
    echo json_encode([
        "success" => true,
        "message" => "Data pengguna berhasil disinkronkan ke database portal.",
        "serverSyncedAt" => round(microtime(true) * 1000)
    ]);
    exit();
}

if (strpos($requestUri, '/withdrawals') !== false) {
    checkAuth($API_SECRET_KEY);
    if ($method === 'GET') {
        echo json_encode([
            "success" => true,
            "total" => 0,
            "data" => []
        ]);
    } else if ($method === 'POST') {
        echo json_encode(["success" => true, "message" => "Penarikan tersimpan."]);
    }
    exit();
}

// Default 404
http_response_code(404);
echo json_encode(["success" => false, "message" => "Endpoint tidak ditemukan."]);
?>
