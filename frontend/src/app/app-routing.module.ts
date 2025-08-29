import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login.component';
import { RegisterPatientComponent } from './auth/register-patient.component';
import { RegisterDoctorComponent } from './auth/register-doctor.component';
import { DoctorProfileComponent } from './doctors/doctor-profile.component';
import { PatientDashboardComponent } from './patient/patient-dashboard.component';
import { DoctorDashboardComponent } from './doctor/doctor-dashboard.component';

const routes: Routes = [
  { path: '', loadComponent: () => import('./home/home.component').then(m => m.HomeComponent) },
  { path: 'login', component: LoginComponent },
  { path: 'register/patient', component: RegisterPatientComponent },
  { path: 'register/doctor', component: RegisterDoctorComponent },
  { path: 'doctors/:id', component: DoctorProfileComponent },
  { path: 'patient/:id', component: PatientDashboardComponent },
  { path: 'doctor/:id', component: DoctorDashboardComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
