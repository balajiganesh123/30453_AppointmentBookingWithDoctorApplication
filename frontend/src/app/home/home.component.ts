import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
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
