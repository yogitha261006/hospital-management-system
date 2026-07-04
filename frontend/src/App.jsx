import { HashRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import Layout from './components/Layout';
import Admissions from './pages/Admissions';
import Patients from './pages/Patients';
import Staff from './pages/Staff';
import AuditLog from './pages/AuditLog';

function App() {
  return (
    <HashRouter>
      <Toaster position="top-right" />
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Navigate to="/admissions" replace />} />
          <Route path="admissions" element={<Admissions />} />
          <Route path="patients" element={<Patients />} />
          <Route path="staff" element={<Staff />} />
          <Route path="audit-log" element={<AuditLog />} />
        </Route>
      </Routes>
    </HashRouter>
  );
}

export default App;
