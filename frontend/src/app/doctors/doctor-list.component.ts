import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-doctor-list',
  templateUrl: './doctor-list.component.html'
})
export class DoctorListComponent implements OnInit {
  f = this.fb.group({ specialization: [''] });
  doctors: any[] = [];
  specializations: string[] = [];

  constructor(private fb: FormBuilder, private api: ApiService) {}

  ngOnInit() {
    this.loadSpecializations();
    this.search();
  }

  loadSpecializations() {
    this.api.getSpecializations().subscribe((list: any) => {
      this.specializations = (list || []).filter((s: any) => !!s).sort();
    });
  }

  search() {
    const p: any = {};
    const spec = (this.f.value.specialization || '').trim();
    if (spec) p.specialization = spec;
    this.api.searchDoctors(p).subscribe((d: any) => this.doctors = d);
  }
}
