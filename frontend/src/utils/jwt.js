/**
 * 简单解码 JWT Payload 并返回一个 Object
 */
export function parseJwt(token) {
    // parts[0] 是 header 的 Base64Url
    // parts[1] 是 payload 的 Base64Url
    // parts[2] 是 signature 的 Base64Url
    try {
        // token 形如 "header.payload.signature"
        const parts = token.split('.');
        if (parts.length !== 3) {
            throw new Error('非法的 JWT 格式');
        }
        // 取第二部分（payload），Base64Url 解码
        // 为了能被浏览器自带的 atob() 正常解码
        const payload = parts[1]
            .replace(/-/g, '+')
            .replace(/_/g, '/');
        const json = decodeURIComponent(
            atob(payload)
                .split('')
                .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                .join('')
        );
        return JSON.parse(json);
    } catch (e) {
        console.error('parseJwt 失败', e);
        return {};
    }
}
