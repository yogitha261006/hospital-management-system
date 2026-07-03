import { useState, useEffect } from 'react';
import { Plus, Check, X } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../api';

const Staff = () => {
  const [staff, setStaff] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [newStaff, setNewStaff] = useState({ name: '', role: 'DOCTOR', employeeCode: '' });

  const fetchStaff = async () => {
    try {
      const response = await api.get('/staff');
      setStaff(response.data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStaff();
  }, []);

  const handleAddStaff = async (e) => {
    e.preventDefault();
    try {
      await api.post('/staff', newStaff);
      toast.success('Staff member added successfully');
      setIsAddModalOpen(false);
      setNewStaff({ name: '', role: 'DOCTOR', employeeCode: '' });
      fetchStaff();
    } catch (error) {
      // Handled by interceptor
    }
  };

  const toggleAvailability = async (id, currentAvailability) => {
    try {
      await api.post(`/staff/${id}/availability?available=${!currentAvailability}`);
      toast.success('Availability updated');
      fetchStaff();
    } catch (error) {
      // Handled by interceptor
    }
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 tracking-tight">Staff Roster</h1>
          <p className="text-slate-500 mt-1">Manage personnel, roles, and shift availability.</p>
        </div>
        <button 
          onClick={() => setIsAddModalOpen(true)}
          className="bg-[#017a74] hover:bg-[#01655f] text-white px-5 py-2.5 rounded-xl font-medium flex items-center gap-2 transition-colors shadow-sm"
        >
          <Plus size={20} /> Add Staff Member
        </button>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-slate-50 border-b border-slate-200 text-slate-500 text-sm">
                <th className="p-4 font-semibold">Employee Code</th>
                <th className="p-4 font-semibold">Name</th>
                <th className="p-4 font-semibold">Role</th>
                <th className="p-4 font-semibold">Current Load</th>
                <th className="p-4 font-semibold">Availability</th>
                <th className="p-4 font-semibold text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="6" className="p-8 text-center text-slate-500">Loading staff...</td>
                </tr>
              ) : staff.length === 0 ? (
                <tr>
                  <td colSpan="6" className="p-8 text-center text-slate-500">No staff found.</td>
                </tr>
              ) : (
                staff.map((s) => (
                  <tr key={s.staffId} className="hover:bg-slate-50/50 transition-colors text-sm">
                    <td className="p-4 text-slate-500 font-medium font-mono">{s.employeeCode}</td>
                    <td className="p-4 font-semibold text-slate-800">{s.name}</td>
                    <td className="p-4">
                      <span className={`px-3 py-1 rounded-full text-xs font-bold tracking-wide ${
                        s.role === 'DOCTOR' ? 'bg-blue-50 text-blue-700' :
                        s.role === 'NURSE' ? 'bg-purple-50 text-purple-700' :
                        'bg-slate-100 text-slate-700'
                      }`}>
                        {s.role}
                      </span>
                    </td>
                    <td className="p-4 text-slate-600">
                      {s.role === 'ADMIN' ? '-' : (
                        <span className="font-medium">
                          {s.load} / {s.role === 'DOCTOR' ? 12 : 8} patients
                        </span>
                      )}
                    </td>
                    <td className="p-4">
                      {s.available ? (
                        <span className="flex items-center gap-1.5 text-emerald-700 bg-emerald-50 px-3 py-1 rounded-full text-xs font-bold w-max border border-emerald-100">
                          <Check size={14} /> On Duty
                        </span>
                      ) : (
                        <span className="flex items-center gap-1.5 text-slate-500 bg-slate-50 px-3 py-1 rounded-full text-xs font-bold w-max border border-slate-200">
                          <X size={14} /> Off Duty
                        </span>
                      )}
                    </td>
                    <td className="p-4 text-right">
                      <button
                        onClick={() => toggleAvailability(s.staffId, s.available)}
                        className={`px-4 py-1.5 rounded-lg text-xs font-semibold transition-colors ${
                          s.available 
                            ? 'text-slate-600 bg-slate-100 hover:bg-slate-200' 
                            : 'text-emerald-700 bg-emerald-50 hover:bg-emerald-100 border border-emerald-200'
                        }`}
                      >
                        {s.available ? 'Clock Out' : 'Clock In'}
                      </button>
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
              <h2 className="text-xl font-bold text-slate-800">Add Staff Member</h2>
              <button onClick={() => setIsAddModalOpen(false)} className="text-slate-400 hover:text-slate-600">
                <X size={24} />
              </button>
            </div>
            <form onSubmit={handleAddStaff} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Employee Code</label>
                <input 
                  required
                  type="text" 
                  placeholder="e.g. EMP-101"
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-[#017a74] focus:border-[#017a74] outline-none transition-all font-mono"
                  value={newStaff.employeeCode}
                  onChange={e => setNewStaff({...newStaff, employeeCode: e.target.value})}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Full Name</label>
                <input 
                  required
                  type="text" 
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-[#017a74] outline-none transition-all"
                  value={newStaff.name}
                  onChange={e => setNewStaff({...newStaff, name: e.target.value})}
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Role</label>
                <select 
                  className="w-full px-4 py-2 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-[#017a74] outline-none"
                  value={newStaff.role}
                  onChange={e => setNewStaff({...newStaff, role: e.target.value})}
                >
                  <option value="DOCTOR">Doctor (Max load: 12)</option>
                  <option value="NURSE">Nurse (Max load: 8)</option>
                  <option value="ADMIN">Administrator</option>
                </select>
              </div>
              <div className="pt-4 flex justify-end gap-3">
                <button 
                  type="button" 
                  onClick={() => setIsAddModalOpen(false)}
                  className="px-5 py-2.5 text-slate-600 hover:bg-slate-100 rounded-xl font-medium transition-colors"
                >
                  Cancel
                </button>
                <button 
                  type="submit" 
                  className="px-5 py-2.5 bg-[#017a74] hover:bg-[#01655f] text-white rounded-xl font-medium shadow-sm transition-colors"
                >
                  Register Staff
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Staff;
