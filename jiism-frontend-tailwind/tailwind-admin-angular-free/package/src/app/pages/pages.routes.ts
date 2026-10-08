import { Routes } from '@angular/router';
import { JiismDashboardComponent } from './jiism/dashboard/dashboard.component';
import { ArticlesComponent } from './jiism/articles/articles.component';
import { AssignmentsComponent } from './jiism/assignments/assignments.component';
import { AnnouncementsComponent } from './jiism/announcements/announcements.component';
import { UsersComponent } from './jiism/users/users.component';
import { PublicComponent } from './jiism/public/public.component';
import { ProfileComponent } from './jiism/profile/profile.component';

export const PagesRoutes: Routes = [
  { path: '', component: JiismDashboardComponent, data: { title: 'Dashboard JIISM' } },
  { path: 'articles', component: ArticlesComponent, data: { title: 'Articles' } },
  { path: 'articles/submit', component: ArticlesComponent, data: { title: 'Soumettre un article' } },
  { path: 'assignments', component: AssignmentsComponent, data: { title: 'Assignations' } },
  { path: 'announcements', component: AnnouncementsComponent, data: { title: 'Annonces' } },
  { path: 'users', component: UsersComponent, data: { title: 'Utilisateurs' } },
  { path: 'profile', component: ProfileComponent, data: { title: 'Profil' } },
  { path: 'publications', component: PublicComponent, data: { title: 'Publications JIISM' } },
];
