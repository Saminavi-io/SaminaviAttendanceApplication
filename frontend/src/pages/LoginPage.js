import React, { useState, useEffect } from 'react';
import './LoginPage.css';
import { useNavigate, Link } from 'react-router-dom';
import { login } from '../api';

function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [redirecting, setRedirecting] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (redirecting) {
      navigate('/register', { state: { username } });
    }
  }, [redirecting, navigate, username]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username || !password) {
      setError('Please enter both username and password.');
      return;
    }
    setError('');
    setRedirecting(false);
    try {
      await login(username, password);
      navigate('/dashboard');
    } catch (err) {
      // Log error for debugging
      console.error('Login error:', err, err.response);
      if (err.response && err.response.status === 401) {
        setError('New user needs to register first.');
        setRedirecting(true);
      } else {
        const msg = err.message || 'Login failed. Please try again.';
        setError(msg);
      }
    }
  };

  return (
    <div className="login-page">
      <h2>Login</h2>
      <form className="login-form" onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={e => setUsername(e.target.value)}
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={e => setPassword(e.target.value)}
        />
        <button type="submit" disabled={redirecting}>Login</button>
        {error && <div className="error-message">{error}</div>}
      </form>
      <div style={{ marginTop: '1rem' }}>
        <span>New employee? </span>
        <Link to="/register">Register here</Link>
      </div>
    </div>
  );
}

export default LoginPage; 