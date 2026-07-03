import { useState, useEffect } from 'react';
import { Bed, Activity, UserPlus, LogOut, Check, X } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../api';

const Admissions = () => {
  const [patients, setPatients] = useState([]);
  const [staff, setStaff] = useState([]);
  const [loading, setLoading] = useState(true);
  const [admitModal, setAdmitModal] = useState({ open: false, patientId: null });
  const [admitData, setAdmitData] = useState({ ward: 'General', bed: '' });

  const fetchData = async () => {
    try {
      const [patientsRes, staffRes] = await Promise.all([
        api.get('/patients'),
        api.get('/staff')
      ]);
      setPatients(patientsRes.data);
      setStaff(staffRes.data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleAssignDoctor = async (patientId, staffId) => {
    if (!staffId) return;
    try {
      await api.post(`/patients/${patientId}/assign/${staffId}`);
      toast.success('Doctor assigned successfully');
      fetchData();
    } catch (error) {}
  };

  const handleAdmit = async (e) => {
    e.preventDefault();
    try {
      await api.post(`/patients/${admitModal.patientId}/admit?ward=${admitData.ward}&bed=${admitData.bed}`);
      toast.success('Patient admitted successfully');
      setAdmitModal({ open: false, patientId: null });
      setAdmitData({ ward: 'General', bed: '' });
      fetchData();
    } catch (error) {}
  };

  const handleDischarge = async (id) => {
    try {
      await api.post(`/patients/${id}/discharge`);
      toast.success('Patient discharged successfully');
      fetchData();
    } catch (error) {}
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Admissions Control</h1>
          <p className="text-sm text-slate-500 mt-1">Manage state transitions (Admit, Assign, Discharge).</p>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-slate-50 border-b border-slate-200 text-slate-500 text-xs uppercase tracking-wider">
                <th className="p-4 font-semibold">Patient</th>
                <th className="p-4 font-semibold">Location</th>
                <th className="p-4 font-semibold">Care Provider</th>
                <th className="p-4 font-semibold text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="4" className="p-8 text-center text-slate-500 text-sm">Loading data...</td>
                </tr>
              ) : patients.length === 0 ? (
                <tr>
                  <td colSpan="4" className="p-8 text-center text-slate-500 text-sm">No patients found.</td>
                </tr>
              ) : (
                patients.map((p) => (
                  <tr key={p.patientId} className="hover:bg-slate-50/50 transition-colors text-sm">
                    <td className="p-4">
                      <div className="font-semibold text-slate-900">{p.name}</div>
                      <div className="text-xs text-slate-500 font-mono mt-0.5">{p.mrn}</div>
                    </td>
                    <td className="p-4">
                      {p.currentStatus === 'ADMITTED' ? (
                        <div className="flex items-center gap-2">
                          <Bed size={16} className="text-[#017a74]" />
                          <span className="text-slate-700">{p.ward} • {p.bed}</span>
                        </div>
                      ) : (
                        <span className="text-slate-400 italic">Not admitted</span>
                      )}
                    </td>
                    <td className="p-4">
                      {p.assignedDoctorName ? (
                        <div className="flex items-center gap-2 text-slate-700 bg-slate-50 px-3 py-1.5 rounded-lg border border-slate-200 w-max">
                           <UserPlus size={14} className="text-slate-400"/> {p.assignedDoctorName}
                        </div>
                      ) : (
                        <select 
                          className="bg-white border border-slate-200 text-slate-600 text-sm rounded-lg px-3 py-1.5 outline-none focus:ring-2 focus:ring-[#017a74] focus:border-[#017a74] cursor-pointer"
                          onChange={(e) => handleAssignDoctor(p.patientId, e.target.value)}
                          defaultValue=""
                          disabled={p.currentStatus === 'DISCHARGED'}
                        >
                          <option value="" disabled>Assign care provider...</option>
                          {staff.filter(s => (s.role === 'DOCTOR' || s.role === 'NURSE') && s.available).map(s => (
                            <option key={s.staffId} value={s.staffId}>{s.name} ({s.role})</option>
                          ))}
                        </select>
                      )}
                    </td>
                    <td className="p-4 text-right space-x-2">
                      <button
                        onClick={() => setAdmitModal({ open: true, patientId: p.patientId })}
                        className="px-4 py-1.5 rounded-lg text-xs font-semibold text-[#017a74] bg-[#f0f9f9] border border-[#b2dfdb] hover:bg-[#e0f2f1] transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                        disabled={p.currentStatus === 'ADMITTED' || p.currentStatus === 'DISCHARGED'}
                      >
                        Admit
                      </button>
                      <button
                        onClick={() => handleDischarge(p.patientId)}
                        className="px-4 py-1.5 rounded-lg text-xs font-semibold text-rose-700 bg-rose-50 border border-rose-200 hover:bg-rose-100 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                        disabled={p.currentStatus !== 'ADMITTED'}
                      >
                        Discharge
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {admitModal.open && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl w-full max-w-sm shadow-xl animate-in zoom-in-95 duration-200">
            <div className="p-5 border-b border-slate-100 flex justify-between items-center">
              <h2 className="text-lg font-bold text-slate-900 flex items-center gap-2">
                <Activity size={18} className="text-[#017a74]" /> Admit Patient
              </h2>
              <button onClick={() => setAdmitModal({ open: false, patientId: null })} className="text-slate-400 hover:text-slate-600">
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleAdmit} className="p-5 space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Ward</label>
                <select 
                  required
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-[#017a74] outline-none text-sm"
                  value={admitData.ward}
                  onChange={e => setAdmitData({...admitData, ward: e.target.value})}
                >
                  <option value="General">General</option>
                  <option value="ICU">ICU</option>
                  <option value="Pediatrics">Pediatrics</option>
                  <option value="Maternity">Maternity</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Bed Number</label>
                <input 
                  required type="text" 
                  placeholder="e.g. B-12"
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-[#017a74] outline-none text-sm font-mono"
                  value={admitData.bed}
                  onChange={e => setAdmitData({...admitData, bed: e.target.value})}
                />
              </div>
              <div className="pt-2 flex justify-end gap-2">
                <button 
                  type="button" 
                  onClick={() => setAdmitModal({ open: false, patientId: null })}
                  className="px-4 py-2 text-slate-600 hover:bg-slate-100 rounded-lg text-sm font-medium transition-colors"
                >
                  Cancel
                </button>
                <button 
                  type="submit" 
                  className="px-4 py-2 bg-[#017a74] hover:bg-[#01655f] text-white rounded-lg text-sm font-medium shadow-sm transition-colors flex items-center gap-1.5"
                >
                  <Check size={16} /> Confirm
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Admissions;
