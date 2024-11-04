import React, { Component } from "react";
import "./Contact.css"

class Contact extends Component {
  render() {
    return (
      <section className="contact-section">
        <h1>Contact Us</h1>
        <p>
          We're here to help and answer any questions you might have. Reach out to us and we'll respond as soon as we can.
        </p>

        <div className="contact-details">
          <h2>Our Office</h2>
          <p>123 Main Street</p>
          <p>City ville, State 12345</p>
          <p>Email: support@ourservice.com</p>
          <p>Phone: (123) 456-7890</p>
        </div>

        <h2>Send Us a Message</h2>
        <form className="contact-form">
          <label>
            Name:
            <input type="text" name="name" placeholder="Your Name" required />
          </label>
          <label>
            Email:
            <input type="email" name="email" placeholder="Your Email" required />
          </label>
          <label>
            Message:
            <textarea name="message" placeholder="Your Message" required />
          </label>
          <button type="submit">Submit</button>
        </form>
      </section>
    );
  }
}

export default Contact;
