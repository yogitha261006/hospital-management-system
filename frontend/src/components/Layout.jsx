import { Outlet, NavLink } from 'react-router-dom';
import { Users, Bed, Activity, Stethoscope, ShieldCheck, HeartPulse } from 'lucide-react';
import { useState, useEffect } from 'react';
import api from '../api';

const Layout = () => {
  const [stats, setStats] = useState({
    totalPatients: 0,
    admittedPatients: 0,
    dischargedPatients: 0,
    availableStaff: 0
  });

  const fetchStats = async () => {
    try {
      const response = await api.get('/dashboard');
      setStats(response.data);
    } catch (error) {
      console.error("Failed to fetch stats", error);
    }
  };

  useEffect(() => {
    fetchStats();
    // In a real app we'd use WebSockets or Polling. 
    // Here we'll poll every 2 seconds for a lively demo feel.
    const interval = setInterval(fetchStats, 2000);
    return () => clearInterval(interval);
  }, []);

  const navLinkClass = ({ isActive }) => 
    `px-6 py-2 rounded-full text-sm font-medium transition-colors ${
      isActive 
        ? 'bg-white text-slate-900 shadow-sm' 
        : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
    }`;

  return (
    <div className="min-h-screen bg-[#fcfbf8] font-sans flex flex-col items-center">
      
      {/* Header */}
      <header className="w-full bg-white border-b border-slate-200 py-4 px-8 flex justify-between items-center">
        <div className="flex items-center space-x-3">
          <div className="bg-[#017a74] p-2 rounded-lg">
            <HeartPulse className="w-6 h-6 text-white" />
          </div>
          <div>
            <h1 className="text-xl font-semibold text-slate-900">Hospital</h1>
            <p className="text-xs text-slate-500">Admissions Domain Model</p>
          </div>
        </div>
        <div className="flex items-center space-x-2 text-sm text-slate-600">
          <ShieldCheck className="w-4 h-4 text-[#017a74]" />
          <span>Encapsulated · Validated · Audited</span>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="w-full max-w-5xl flex-1 py-8 px-4 flex flex-col space-y-8">
        
        {/* Stats Grid */}
        <div className="grid grid-cols-4 gap-4">
          <div className="bg-white rounded-xl border border-slate-200 p-4 flex flex-col justify-between shadow-sm h-24">
            <div className="flex justify-between items-start">
              <span className="text-sm text-slate-500">Patients</span>
              <div className="bg-slate-100 p-1.5 rounded-full"><Users className="w-4 h-4 text-slate-600" /></div>
            </div>
            <span className="text-3xl font-semibold text-slate-900">{stats.totalPatients}</span>
          </div>
          
          <div className="bg-[#f0f9f9] rounded-xl border border-[#b2dfdb] p-4 flex flex-col justify-between shadow-sm h-24">
            <div className="flex justify-between items-start">
              <span className="text-sm text-[#017a74]">Admitted</span>
              <div className="bg-white p-1.5 rounded-full shadow-sm"><Bed className="w-4 h-4 text-[#017a74]" /></div>
            </div>
            <span className="text-3xl font-semibold text-slate-900">{stats.admittedPatients}</span>
          </div>

          <div className="bg-white rounded-xl border border-slate-200 p-4 flex flex-col justify-between shadow-sm h-24">
            <div className="flex justify-between items-start">
              <span className="text-sm text-slate-500">Discharged</span>
              <div className="bg-slate-100 p-1.5 rounded-full"><Activity className="w-4 h-4 text-slate-600" /></div>
            </div>
            <span className="text-3xl font-semibold text-slate-900">{stats.dischargedPatients}</span>
          </div>

          <div className="bg-white rounded-xl border border-slate-200 p-4 flex flex-col justify-between shadow-sm h-24">
            <div className="flex justify-between items-start">
              <span className="text-sm text-slate-500">Staff on duty</span>
              <div className="bg-slate-100 p-1.5 rounded-full"><Stethoscope className="w-4 h-4 text-slate-600" /></div>
            </div>
            <span className="text-3xl font-semibold text-slate-900">{stats.availableStaff}</span>
          </div>
        </div>

        {/* Navigation */}
        <div className="bg-[#f1f5f9] p-1 rounded-full flex w-full">
          <NavLink to="/admissions" className={({ isActive }) => `flex-1 text-center ${navLinkClass({ isActive })}`}>
            Admissions
          </NavLink>
          <NavLink to="/patients" className={({ isActive }) => `flex-1 text-center ${navLinkClass({ isActive })}`}>
            Patients
          </NavLink>
          <NavLink to="/staff" className={({ isActive }) => `flex-1 text-center ${navLinkClass({ isActive })}`}>
            Staff
          </NavLink>
          <NavLink to="/audit-log" className={({ isActive }) => `flex-1 text-center ${navLinkClass({ isActive })}`}>
            Audit log
          </NavLink>
        </div>

        {/* Dynamic Page Content */}
        <div className="flex-1">
          <Outlet />
        </div>

        {/* Footer */}
        <footer className="mt-12 bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
          <h3 className="text-sm font-semibold text-slate-900 mb-4">Domain guarantees</h3>
          <div className="grid grid-cols-2 gap-x-8 gap-y-2 text-xs text-slate-600">
            <p>· Sensitive fields are ECMAScript hard-private (#field).</p>
            <p>· Getters return frozen snapshots — no mutable references leak.</p>
            <p>· State transitions are gated: REGISTERED → ADMITTED → DISCHARGED.</p>
            <p>· Staff/patient assignment is coordinated with rollback on failure.</p>
            <p>· Every mutation appends an immutable audit entry.</p>
            <p>· Patient and Staff aggregates are decoupled — mediated by Hospital service.</p>
          </div>
        </footer>
      </main>
    </div>
  );
};

export default Layout;
