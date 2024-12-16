import React from 'react';

const Footer = () => {
  return (
    <footer>
      <div
        className="text-info"
        style={{
          display: "flex",
          flexWrap: "wrap",
          gap: "1rem",
          justifyContent: "center",
        }}
      >
        <p>
          © 2023 - {new Date().getFullYear()} AiPower ,Inc. All rights
          reserved.
        </p>
        <a href="/terms-of-service">Terms of Service</a>
        <a href="/privacy-policy">Privacy Policy</a>
        <a href="/cookie-policy">Cookie Policy</a>
        <a href="/about">About Us</a>
        <a href="/press-contact">Press Contact</a>
        <a href="/careers">Careers</a>
        <a href="/press-releases">Press Releases</a>
        <a href="/investor-relations">Investor Relations</a>
        <a href="/affiliate-program">Affiliate Program</a>
        <a href="/faqs">FAQs</a>
        <a href="/sitemap">Sitemap</a>
        <a href="/security">Security</a>
      </div>
    </footer>
  );
};

export default Footer;
