import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { LoginComponent } from './auth/login.component';
import { RegisterPatientComponent } from './auth/register-patient.component';
import { RegisterDoctorComponent } from './auth/register-doctor.component';
import { DoctorListComponent } from './doctors/doctor-list.component';
import { DoctorProfileComponent } from './doctors/doctor-profile.component';
import { PatientDashboardComponent } from './patient/patient-dashboard.component';
import { DoctorDashboardComponent } from './doctor/doctor-dashboard.component';

@NgModule({
  declarations: [AppComponent, LoginComponent, RegisterPatientComponent, RegisterDoctorComponent, DoctorListComponent, DoctorProfileComponent, PatientDashboardComponent, DoctorDashboardComponent],
  imports: [BrowserModule, FormsModule, ReactiveFormsModule, HttpClientModule, AppRoutingModule],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {}
