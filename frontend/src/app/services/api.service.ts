import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ApiService {
  base = '/api';

  constructor(private http: HttpClient) {}

  login(body: any) { return this.http.post(this.base + '/auth/login', body); }
  registerPatient(body: any) { return this.http.post(this.base + '/auth/register/patient', body); }
  registerDoctor(body: any) { return this.http.post(this.base + '/auth/register/doctor', body); }

  searchDoctors(params: any) {
    return this.http.get(this.base + '/doctors', { params });
  }

  getSpecializations() {
    return this.http.get<string[]>(this.base + '/doctors/specializations');
  }

  getDoctor(id: number) { return this.http.get(this.base + '/doctors/' + id); }
  addSchedule(id: number, body: any) { return this.http.post(this.base + '/doctors/' + id + '/schedules', body); }
  addBreak(id: number, body: any) { return this.http.post(this.base + '/doctors/' + id + '/breaks', body); }
  addLeave(id: number, body: any) { return this.http.post(this.base + '/doctors/' + id + '/leaves', body); }

  slots(id: number, date: string) {
    const tz = Intl.DateTimeFormat().resolvedOptions().timeZone || 'UTC';
    return this.http.get(this.base + '/doctors/' + id + '/slots', { params: { date, tz } });
  }

  acceptAppointment(id: number) { return this.http.put(this.base + '/appointments/' + id + '/accept', {}); }
  completeAppointment(id: number) { return this.http.put(this.base + '/appointments/' + id + '/complete', {}); }
  cancelAppointment(id: number) { return this.http.put(this.base + '/appointments/' + id + '/cancel', {}); }
  updateStatus(id: number, status: string) { return this.http.put(this.base + '/appointments/' + id + '/status', { status }); }
  patientAppointments(id: number) { return this.http.get(this.base + '/appointments/patient/' + id); }

  doctorToday(id: number) {
    const tz = Intl.DateTimeFormat().resolvedOptions().timeZone || 'UTC';
    return this.http.get(this.base + '/doctors/' + id + '/appointments/today', { params: { tz } });
  }

  deleteAppointment(id: number, patientUserId: number) {
    return this.http.delete(this.base + '/appointments/' + id + '/patient/' + patientUserId);
  }

  doctorUpcomingByUser(userId: number) {
    return this.http.get(this.base + '/appointments/doctor-user/' + userId + '/upcoming');
  }

  doctorAllByUser(userId: number) {
    return this.http.get(this.base + '/appointments/doctor-user/' + userId + '/all');
  }

  getDoctorByUser(userId: number) {
    return this.http.get(this.base + '/doctors/by-user/' + userId);
  }

  getSlots(doctorId: number, date: string) {
    const tz = Intl.DateTimeFormat().resolvedOptions().timeZone;
    return this.http.get(this.base + `/doctors/${doctorId}/slots`, { params: { date, tz } });
  }

  createAppointment(payload: any) {
    return this.http.post(this.base + '/appointments', payload);
  }
}
