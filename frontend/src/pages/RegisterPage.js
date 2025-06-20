import React, { useState } from 'react';
import './RegisterPage.css';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';

function RegisterPage() {
  const location = useLocation();
  const [form, setForm] = useState({
    username: location.state?.username || '',
    password: '',
    name: '',
    email: '',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [generatedId, setGeneratedId] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setGeneratedId('');
    setLoading(true);
    try {
      const res = await axios.post('http://localhost:9080/api/auth/register', form);
      setSuccess('Registration successful! You can now log in.');
      // Extract employee ID from response if present
      const match = res.data.match(/Employee ID: (\w+)/);
      if (match) setGeneratedId(match[1]);
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      setError((err.response && err.response.data) ? err.response.data : 'Registration failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-page">
      <h2>Register</h2>
      <form className="register-form" onSubmit={handleSubmit}>
        <input name="username" type="text" placeholder="Username" value={form.username} onChange={handleChange} required />
        <input name="password" type="password" placeholder="Password" value={form.password} onChange={handleChange} required />
        <input name="name" type="text" placeholder="Full Name" value={form.name} onChange={handleChange} required />
        <input name="email" type="email" placeholder="Email" value={form.email} onChange={handleChange} required />
        <button type="submit" disabled={loading}>{loading ? 'Registering...' : 'Register'}</button>
        {error && <div className="error-message">{error}</div>}
        {success && <div className="success-message">{success}</div>}
        {generatedId && <div className="success-message">Your Employee ID: <b>{generatedId}</b></div>}
      </form>
    </div>
  );
}

export default RegisterPage; 