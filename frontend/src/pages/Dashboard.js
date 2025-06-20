import React, { useEffect, useState } from 'react';
import './Dashboard.css';
import { fetchAttendanceHistory, fetchTodayAttendance, clockIn, clockOut, getToken } from '../api';
import { useNavigate } from 'react-router-dom';

function Dashboard() {
  const [attendance, setAttendance] = useState([]);
  const [today, setToday] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showHistory, setShowHistory] = useState(false);
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    if (!getToken()) {
      navigate('/login');
      return;
    }
    loadData();
    // eslint-disable-next-line
  }, [navigate]);

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [history, todayStatus] = await Promise.all([
        fetchAttendanceHistory(),
        fetchTodayAttendance().catch(() => null),
      ]);
      setAttendance(history);
      setToday(todayStatus);
    } catch (err) {
      setError(err.message || 'Failed to fetch attendance data.');
    } finally {
      setLoading(false);
    }
  };

  const handleClockIn = async () => {
    setError('');
    setLoading(true);
    try {
      await clockIn();
      await loadData();
    } catch (err) {
      setError((err && err.message) ? `Clock in failed: ${err.message}` : 'Clock in failed.');
    } finally {
      setLoading(false);
    }
  };

  const handleClockOut = async () => {
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      await clockOut();
      setSuccess('Clock out successful!');
      await loadData();
    } catch (err) {
      setError((err && err.message) ? `Clock out failed: ${err.message}` : 'Clock out failed.');
    } finally {
      setLoading(false);
    }
  };

  const isClockedIn = today && today.clockInTime && !today.clockOutTime;
  const isClockedOut = today && today.clockInTime && today.clockOutTime;

  return (
    <div className="dashboard-page">
      <h2>Welcome to the Saminavi Attendance Dashboard</h2>
      {error && <div className="error-message">{error}</div>}
      {success && <div className="success-message">{success}</div>}
      {loading && <div>Loading...</div>}
      <div className="today-status">
        <h3>Today's Status</h3>
        {today ? (
          <>
            <div>Date: {today.date}</div>
            <div>Clock In: {today.clockInTime || '-'}</div>
            <div>Clock Out: {today.clockOutTime || '-'}</div>
            <div>
              {!today.clockInTime && (
                <button onClick={handleClockIn} disabled={loading}>Clock In</button>
              )}
              {isClockedIn && (
                <button onClick={handleClockOut} disabled={loading}>Clock Out</button>
              )}
              {isClockedOut && <span>✔️ You have clocked out for today.</span>}
            </div>
          </>
        ) : (
          <div>
            <button onClick={handleClockIn} disabled={loading}>Clock In</button>
          </div>
        )}
      </div>
      <button className="history-btn" onClick={() => setShowHistory(h => !h)}>
        {showHistory ? 'Hide Attendance History' : 'Show Attendance History'}
      </button>
      {showHistory && (
        <>
          <h3>Attendance History</h3>
          {attendance.length > 0 ? (
            <table className="attendance-table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Clock In</th>
                  <th>Clock Out</th>
                  <th>Total Hours</th>
                </tr>
              </thead>
              <tbody>
                {attendance.map((record, idx) => (
                  <tr key={idx}>
                    <td>{record.date}</td>
                    <td>{record.clockInTime || '-'}</td>
                    <td>{record.clockOutTime || '-'}</td>
                    <td>{record.clockOutTime && record.clockInTime ? calculateHours(record.clockInTime, record.clockOutTime) : '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : !error && !loading ? (
            <p>No attendance records found.</p>
          ) : null}
        </>
      )}
    </div>
  );
}

function calculateHours(clockIn, clockOut) {
  // Assumes format: 'HH:mm:ss' or ISO string
  try {
    const inTime = new Date(`1970-01-01T${clockIn}`);
    const outTime = new Date(`1970-01-01T${clockOut}`);
    const diffMs = outTime - inTime;
    const hours = Math.floor(diffMs / 3600000);
    const minutes = Math.floor((diffMs % 3600000) / 60000);
    return `${hours}h ${minutes}m`;
  } catch {
    return '-';
  }
}

export default Dashboard; 