import React from 'react';
import { Route, Routes, Navigate, Link, useNavigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import Dashboard from './pages/Dashboard';
import './App.css';
import { getToken, removeToken } from './api';

function App() {
  const navigate = useNavigate();
  const loggedIn = !!getToken();

  const handleLogout = () => {
    removeToken();
    navigate('/login');
  };

  return (
    <div className="app-container">
      <nav className="navbar">
        <div className="logo">
          <img src={require('./saminavi-logo.png')} alt="Saminavi Logo" height={40} />
        </div>
        <div className="nav-links">
          <Link to="/login">Login</Link>
          <Link to="/register">Register</Link>
          <Link to="/dashboard">Dashboard</Link>
          {loggedIn && <button className="logout-btn" onClick={handleLogout}>Logout</button>}
        </div>
      </nav>
      <div className="main-content">
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/" element={<Navigate to="/login" />} />
        </Routes>
      </div>
    </div>
  );
}

export default App;
