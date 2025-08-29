import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type AppointmentStatus = 'PENDING' | 'ACCEPTED' | 'COMPLETED' | 'CANCELLED';

export interface Appointment {
  id: number;
  startTime: string;
  endTime: string;
  status: AppointmentStatus;
  patient?: { user?: { name?: string } };
  doctor?: { user?: { name?: string } };
}

@Injectable({ providedIn: 'root' })
export class AppointmentService {
  private base = '/api/appointments';
  constructor(private http: HttpClient) {}
  doctorUpcoming(userId: number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.base}/doctor-user/${userId}/upcoming`);
  }
  doctorAll(userId: number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.base}/doctor-user/${userId}/all`);
  }
  patientAppointments(userId: number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.base}/patient/${userId}`);
  }
  accept(id: number): Observable<void> {
    return this.http.put<void>(`${this.base}/${id}/accept`, {});
  }
  complete(id: number): Observable<void> {
    return this.http.put<void>(`${this.base}/${id}/complete`, {});
  }
  cancel(id: number): Observable<void> {
    return this.http.put<void>(`${this.base}/${id}/cancel`, {});
  }
}
