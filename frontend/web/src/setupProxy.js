const { createProxyMiddleware } = require("http-proxy-middleware");

module.exports = function (app) {
    // API 관련 요청을 k9e207.p.ssafy.io로 프록시
    app.use(
        "/api",
        createProxyMiddleware({
            target: "https://k9e207.p.ssafy.io",
            changeOrigin: true,
        })
    );

    // 웹소켓 관련 요청을 k9e207a.p.ssafy.io:8090로 프록시
    app.use(
        "/ws",
        createProxyMiddleware({
            target: "https://k9e207a.p.ssafy.io",
            changeOrigin: true,
            ws: true,
        })
    );
};