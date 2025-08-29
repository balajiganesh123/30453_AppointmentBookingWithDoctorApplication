import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ApiService } from '../services/api.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-doctor-profile',
  templateUrl: './doctor-profile.component.html'
})
export class DoctorProfileComponent implements OnInit {
  doctor: any;
  date = new Date().toISOString().slice(0, 10);
  slots: any[] = [];
  fgs: { [k: string]: FormGroup } = {};
  doctorId = 0;
  submitting: { [k: string]: boolean } = {};
  allSlots: { date: string; time: string }[] = [];
  loadingAll = false;
  todayStr = new Date().toISOString().slice(0, 10);

  constructor(private route: ActivatedRoute, private api: ApiService, private fb: FormBuilder) {}

  ngOnInit() {
    this.doctorId = Number(this.route.snapshot.paramMap.get('id'));
    this.api.getDoctor(this.doctorId).subscribe((d: any) => this.doctor = d);
    if (this.date < this.todayStr) this.date = this.todayStr;
    this.findSlots();
  }

  trackSlot(i: number, s: any) { return s.startUtc; }

  private extractUserId(): number | null {
    const raw = localStorage.getItem('user');
    if (!raw) return null;
    let u: any;
    try { u = JSON.parse(raw); } catch { return null; }
    const tryVals = [
      u?.id,
      u?.user?.id,
      u?.userId,
      u?.uid,
      u?.payload?.id,
      u?.data?.id
    ];
    for (const v of tryVals) {
      if (typeof v === 'number' && Number.isFinite(v)) return v;
      if (typeof v === 'string' && v.trim() && !Number.isNaN(Number(v))) return Number(v);
    }
    const fallbackKeys = ['userId','uid','patientId','id'];
    for (const k of fallbackKeys) {
      const s = localStorage.getItem(k);
      if (s && !Number.isNaN(Number(s))) return Number(s);
    }
    return null;
  }

  findSlots() {
    if (this.date < this.todayStr) this.date = this.todayStr;
    this.api.getSlots(this.doctorId, this.date).subscribe((arr: any[]) => {
      this.slots = arr;
      const keep = new Set(arr.map(s => s.startUtc));
      for (const s of arr) {
        const k = s.startUtc;
        if (!this.fgs[k]) this.fgs[k] = this.fb.group({ reason: [''], notes: [''] });
      }
      for (const k of Object.keys(this.fgs)) {
        if (!keep.has(k)) delete this.fgs[k];
      }
    });
  }

  book(s: any) {
    const patientId = this.extractUserId();
    if (!patientId) { alert('Please log in again.'); return; }
    const fg = this.fgs[s.startUtc];
    if (!fg) { this.findSlots(); return; }
    const payload = {
      doctorId: this.doctorId,
      patientId: patientId,
      startUtc: s.startUtc,
      reason: fg.value.reason || '',
      notes: fg.value.notes || ''
    };
    this.submitting[s.startUtc] = true;
    this.api.createAppointment(payload).subscribe({
      next: () => {
        fg.reset();
        this.findSlots();
        this.submitting[s.startUtc] = false;
        alert('Appointment requested.');
      },
      error: () => {
        this.findSlots();
        this.submitting[s.startUtc] = false;
        alert('That slot is no longer available.');
      }
    });
  }

  private toLocalHm(isoUtc: string): string {
    const d = new Date(isoUtc);
    return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
  }

  loadAllAvailableSlots(days: number = 30) {
    const base = new Date(this.date);
    const dates: string[] = [];
    for (let i = 0; i < days; i++) {
      const d = new Date(base.getFullYear(), base.getMonth(), base.getDate() + i);
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, '0');
      const dd = String(d.getDate()).padStart(2, '0');
      dates.push(`${y}-${m}-${dd}`);
    }
    this.loadingAll = true;
    const reqs = dates.map(dt => this.api.getSlots(this.doctorId, dt));
    forkJoin(reqs).subscribe({
      next: res => {
        const rows: { date: string; time: string }[] = [];
        res.forEach((arr: any[], i: number) => {
          const date = dates[i];
          arr.forEach(s => rows.push({ date, time: this.toLocalHm(s.startUtc) }));
        });
        rows.sort((a, b) => a.date === b.date ? a.time.localeCompare(b.time) : a.date.localeCompare(b.date));
        this.allSlots = rows;
        this.loadingAll = false;
      },
      error: () => {
        this.allSlots = [];
        this.loadingAll = false;
      }
    });
  }
}
