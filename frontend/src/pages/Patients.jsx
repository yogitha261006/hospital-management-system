import { useState, useEffect } from 'react';
import { Plus, CheckCircle2, Activity, ShieldCheck } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../api';

const Patients = () => {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  
  const [newPatient, setNewPatient] = useState({ 
    mrn: '', name: '', dateOfBirth: ''
  });

  const fetchPatients = async () => {
    try {
      const response = await api.get('/patients');
      setPatients(response.data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPatients();
  }, []);

  const handleAddPatient = async (e) => {
    e.preventDefault();
    try {
      await api.post('/patients', newPatient);
      toast.success('Patient registered successfully');
      setIsAddModalOpen(false);
      setNewPatient({ mrn: '', name: '', dateOfBirth: '' });
      fetchPatients();
    } catch (error) {}
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Patient Registry</h1>
          <p className="text-sm text-slate-500 mt-1">Register new patients to the hospital system.</p>
        </div>
        <button 
          onClick={() => setIsAddModalOpen(true)}
          className="bg-[#017a74] hover:bg-[#01655f] text-white px-5 py-2 rounded-lg font-medium flex items-center gap-2 transition-colors shadow-sm text-sm"
        >
          <Plus size={18} /> Register Patient
        </button>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-slate-50 border-b border-slate-200 text-slate-500 text-xs uppercase tracking-wider">
                <th className="p-4 font-semibold">MRN</th>
                <th className="p-4 font-semibold">Patient Name</th>
                <th className="p-4 font-semibold">Date of Birth</th>
                <th className="p-4 font-semibold">System Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="4" className="p-8 text-center text-slate-500 text-sm">Loading patients...</td>
                </tr>
              ) : patients.length === 0 ? (
                <tr>
                  <td colSpan="4" className="p-8 text-center text-slate-500 text-sm">No patients registered.</td>
                </tr>
              ) : (
                patients.map((p) => (
                  <tr key={p.patientId} className="hover:bg-slate-50/50 transition-colors text-sm">
                    <td className="p-4 font-mono text-slate-500">{p.mrn}</td>
                    <td className="p-4 font-semibold text-slate-900">{p.name}</td>
                    <td className="p-4 text-slate-600">{p.dateOfBirth}</td>
                    <td className="p-4">
                      {p.currentStatus === 'REGISTERED' ? (
                        <span className="flex items-center gap-1 text-slate-600 bg-slate-100 px-2 py-1 rounded-md text-xs font-medium w-max border border-slate-200">
                          <ShieldCheck size={14} /> Registered
                        </span>
                      ) : p.currentStatus === 'ADMITTED' ? (
                        <span className="flex items-center gap-1 text-blue-700 bg-blue-50 px-2 py-1 rounded-md text-xs font-medium w-max border border-blue-100">
                          <Activity size={14} /> Admitted
                        </span>
                      ) : (
                        <span className="flex items-center gap-1 text-emerald-700 bg-emerald-50 px-2 py-1 rounded-md text-xs font-medium w-max border border-emerald-100">
                          <CheckCircle2 size={14} /> Discharged
                        </span>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {isAddModalOpen && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl w-full max-w-md shadow-xl animate-in zoom-in-95 duration-200">
            <div className="p-6 border-b border-slate-100 flex justify-between items-center">
              <h2 className="text-xl font-bold text-slate-900">Register Patient</h2>
              <button onClick={() => setIsAddModalOpen(false)} className="text-slate-400 hover:text-slate-600">
                <Plus size={24} className="rotate-45" />
              </button>
            </div>
            <form onSubmit={handleAddPatient} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Medical Record Number (MRN)</label>
                <input 
                  required type="text" 
                  placeholder="e.g. MRN-5001"
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-[#017a74] focus:border-[#017a74] outline-none font-mono text-sm"
                  value={newPatient.mrn}
                  onChange={e => setNewPatient({...newPatient, mrn: e.target.value})}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Full Name</label>
                <input 
                  required type="text" 
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-[#017a74] outline-none text-sm"
                  value={newPatient.name}
                  onChange={e => setNewPatient({...newPatient, name: e.target.value})}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Date of Birth</label>
                <input 
                  required type="date"
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-[#017a74] outline-none text-sm"
                  value={newPatient.dateOfBirth}
                  onChange={e => setNewPatient({...newPatient, dateOfBirth: e.target.value})}
                />
              </div>
              <div className="pt-4 flex justify-end gap-3">
                <button 
                  type="button" 
                  onClick={() => setIsAddModalOpen(false)}
                  className="px-5 py-2.5 text-slate-600 hover:bg-slate-100 rounded-xl text-sm font-medium transition-colors"
                >
                  Cancel
                </button>
                <button 
                  type="submit" 
                  className="px-5 py-2.5 bg-[#017a74] hover:bg-[#01655f] text-white rounded-xl text-sm font-medium shadow-sm transition-colors"
                >
                  Register
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Patients;
