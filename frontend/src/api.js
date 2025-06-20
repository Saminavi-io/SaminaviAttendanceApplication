import axios from 'axios';

const API_BASE_URL = 'http://localhost:9080/api';

export function setToken(token) {
  localStorage.setItem('jwt', token);
}

export function getToken() {
  return localStorage.getItem('jwt');
}

export function removeToken() {
  localStorage.removeItem('jwt');
}

export async function login(username, password) {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/login`, {
      username,
      password,
    });
    if (response.data && response.data.token) {
      setToken(response.data.token);
    }
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error;
  }
}

export async function fetchAttendanceHistory() {
  try {
    const token = getToken();
    const response = await axios.get(`${API_BASE_URL}/attendance/history`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error;
  }
}

export async function fetchTodayAttendance() {
  try {
    const token = getToken();
    const response = await axios.get(`${API_BASE_URL}/attendance/today`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error;
  }
}

export async function clockIn() {
  try {
    const token = getToken();
    const response = await axios.post(`${API_BASE_URL}/attendance/clock-in`, {}, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error;
  }
}

export async function clockOut() {
  try {
    const token = getToken();
    const response = await axios.post(`${API_BASE_URL}/attendance/clock-out`, {}, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error;
  }
}

// Add more API functions as needed 