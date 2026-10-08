import { Routes } from '@angular/router';

import { BlankComponent } from './layouts/blank/blank.component';
import { FullComponent } from './layouts/full/full.component';

import { JiismDashboardComponent } from './pages/jiism/dashboard/dashboard.component';
import { ArticlesComponent } from './pages/jiism/articles/articles.component';
import { SubmitArticleComponent } from './pages/jiism/articles/submit.component';
import { AssignmentsComponent } from './pages/jiism/assignments/assignments.component';
import { AnnouncementsComponent } from './pages/jiism/announcements/announcements.component';
import { UsersComponent } from './pages/jiism/users/users.component';
import { PublicComponent } from './pages/jiism/public/public.component';
import { ProfileComponent } from './pages/jiism/profile/profile.component';
import { EditorDashboardComponent } from './pages/jiism/editor-dashboard/editor-dashboard.component';
import { EditorAssignmentsComponent } from './pages/jiism/editor-assignments/editor-assignments.component';
import { EditorArticlesComponent } from './pages/jiism/editor-articles/editor-articles.component';

import { roleGuard } from './guards/role.guard';
import { PublicAnnouncementsComponent } from './pages/jiism/public-announcements/public-announcements.component';
import { PublicLayoutComponent } from './layouts/public/public.component';

export const routes: Routes = [

  // =========================================================
  // ADMIN
  // =========================================================

  {
    path: 'admin',
    component: FullComponent,
    canActivate: [roleGuard(['ADMIN'])],
    children: [

      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },

      {
        path: 'dashboard',
        component: JiismDashboardComponent,
        data: {
          title: 'Dashboard Administrateur'
        }
      },

      {
        path: 'articles',
        component: ArticlesComponent,
        data: {
          title: 'Gestion des articles'
        }
      },

      {
        path: 'assignments',
        component: AssignmentsComponent,
        data: {
          title: 'Gestion éditoriale'
        }
      },

      {
        path: 'announcements',
        component: AnnouncementsComponent,
        data: {
          title: 'Gestion des annonces'
        }
      },

      {
        path: 'users',
        component: UsersComponent,
        data: {
          title: 'Gestion des utilisateurs'
        }
      },

      {
        path: 'publications',
        component: PublicComponent,
        data: {
          title: 'Publications'
        }
      },

      {
        path: 'profile',
        component: ProfileComponent,
        data: {
          title: 'Mon profil'
        }
      }
    ]
  },


  // =========================================================
  // EDITEUR
  // =========================================================

  {
  path: 'editor',
  component: FullComponent,
  canActivate: [roleGuard(['EDITEUR'])],
  children: [
    {
      path: '',
      redirectTo: 'dashboard',
      pathMatch: 'full'
    },

    {
      path: 'dashboard',
      component: EditorDashboardComponent,
      data: { title: 'Dashboard Éditeur' }
    },

    {
      path: 'articles',
      component: EditorArticlesComponent,
      data: { title: 'Mes articles' }
    },

    {
      path: 'assignments',
      component: EditorAssignmentsComponent,
      data: { title: 'Mes assignations' }
    },

    {
      path: 'profile',
      component: ProfileComponent,
      data: { title: 'Mon profil' }
    }
  ]
},


  // =========================================================
  // AUTEUR
  // =========================================================

  {
    path: 'author',
    component: FullComponent,
    canActivate: [roleGuard(['AUTEUR'])],
    children: [

      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },

      {
        path: 'dashboard',
        component: JiismDashboardComponent,
        data: {
          title: 'Dashboard Auteur'
        }
      },

      {
        path: 'articles',
        component: ArticlesComponent,
        data: {
          title: 'Mes articles'
        }
      },

      {
        path: 'articles/submit',
        component: SubmitArticleComponent,
        data: {
          title: 'Soumettre un article'
        }
      },

      {
        path: 'announcements',
        component: AnnouncementsComponent,
        data: {
          title: 'Annonces'
        }
      },

      {
        path: 'profile',
        component: ProfileComponent,
        data: {
          title: 'Mon profil'
        }
      }
    ]
  },


 // =========================================================
// PUBLIC
// =========================================================

{
  path: '',
  component: PublicLayoutComponent,
  children: [

    {
      path: 'publications',
      component: PublicComponent,
      data: {
        title: 'Publications JIISM'
      }
    },

    {
      path: 'announcements',
      component: PublicAnnouncementsComponent,
      data: {
        title: 'Annonces JIISM'
      }
    }

  ]
},
  
  // =========================================================
  // AUTHENTIFICATION
  // =========================================================
  {
    path: '',
    component: BlankComponent,
    children: [
      {
        path: 'authentication',
        loadChildren: () =>
          import('./pages/authentication/authentication.routes')
            .then(m => m.AuthenticationRoutes)
      }
    ]
  },


  // =========================================================
  // TEMPLATE
  // =========================================================

  {
  path: 'ui-components',
  canActivate: [roleGuard(['ADMIN'])],
  loadChildren: () =>
    import('./pages/ui-components/ui-components.routes')
      .then(m => m.UiComponentsRoutes)
},

{
  path: 'extra',
  canActivate: [roleGuard(['ADMIN'])],
  loadChildren: () =>
    import('./pages/extra/extra.routes')
      .then(m => m.ExtraRoutes)
},


  // =========================================================
  // DEFAULT
  // =========================================================

  {
    path: '',
    redirectTo: 'authentication/login',
    pathMatch: 'full'
  },

  {
    path: '**',
    redirectTo: 'authentication/login'
  }
];