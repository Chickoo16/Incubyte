import { Routes } from '@angular/router';

import { EmployeeList } from './employees/employee-list/employee-list';
import { EmployeeDetail } from './employees/employee-detail/employee-detail';
import { EmployeeForm } from './employees/employee-form/employee-form';
import { Dashboard } from './dashboard/dashboard/dashboard';

export const routes: Routes = [
  { path: '', component: EmployeeList },
  { path: 'employees/new', component: EmployeeForm },
  { path: 'employees/:id', component: EmployeeDetail },
  { path: 'dashboard', component: Dashboard },
  { path: '**', redirectTo: '' },
];
