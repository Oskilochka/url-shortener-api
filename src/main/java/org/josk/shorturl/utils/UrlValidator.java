package org.josk.shorturl.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

@Component
public class UrlValidator {
    private static final Logger log = LoggerFactory.getLogger(UrlValidator.class);

    public boolean isValid(String urlString) {
        try {
            URL url = new URL(urlString);
            String protocol = url.getProtocol().toLowerCase();

            if (!protocol.equals("http") && !protocol.equals("https")) {
                log.warn("Invalid protocol: {}", protocol);
                return false;
            }

            String host = url.getHost();
            if (isLocalhost(host)) {
                log.warn("Host is localhost or loopback: {}", host);
                return false;
            }

            InetAddress inetAddress = InetAddress.getByName(host);
            if (inetAddress.isAnyLocalAddress() || inetAddress.isLoopbackAddress() || isPrivateAddress(inetAddress)) {
                log.warn("Host resolves to local or private address: {}", inetAddress.getHostAddress());
                return false;
            }

            return true;
        } catch (MalformedURLException e) {
            log.warn("Malformed URL: {}", urlString);
        } catch (UnknownHostException e) {
            log.warn("Unknown host: {}", urlString);
        } catch (Exception e) {
            log.error("Unexpected error during URL validation: {}", e.toString());
        }

        return false;
    }

    private boolean isLocalhost(String host) {
        return host.equalsIgnoreCase("localhost") ||
                host.equals("127.0.0.1") ||
                host.equals("0.0.0.0") ||
                host.equals("::1");
    }

    private boolean isPrivateAddress(InetAddress address) {
        return address.isSiteLocalAddress()
                || address.isLinkLocalAddress()
                || address.isLoopbackAddress();
    }
}
