import { NavItem } from './nav-item/nav-item';

export const navItems: NavItem[] = [

  // =========================================================
  // ADMIN
  // =========================================================

  {
    navCap: 'ADMINISTRATION',
    roles: ['ADMIN']
  },

  {
    displayName: 'Tableau de bord',
    iconName: 'solar:widget-5-line-duotone',
    route: '/admin/dashboard',
    roles: ['ADMIN']
  },

  {
    displayName: 'Articles',
    iconName: 'solar:document-text-line-duotone',
    route: '/admin/articles',
    roles: ['ADMIN']
  },

  {
    displayName: 'Assignations',
    iconName: 'solar:users-group-two-rounded-line-duotone',
    route: '/admin/assignments',
    roles: ['ADMIN']
  },

  {
    displayName: 'Annonces',
    iconName: 'solar:bell-line-duotone',
    route: '/admin/announcements',
    roles: ['ADMIN']
  },

  {
    displayName: 'Utilisateurs',
    iconName: 'solar:users-group-rounded-line-duotone',
    route: '/admin/users',
    roles: ['ADMIN']
  },

  {
    displayName: 'Publications',
    iconName: 'solar:book-2-line-duotone',
    route: '/admin/publications',
    roles: ['ADMIN']
  },

  {
    displayName: 'Mon profil',
    iconName: 'solar:user-circle-line-duotone',
    route: '/admin/profile',
    roles: ['ADMIN']
  },


  // =========================================================
  // EDITEUR
  // =========================================================

  {
    navCap: 'ESPACE ÉDITEUR',
    roles: ['EDITEUR']
  },

  {
    displayName: 'Tableau de bord',
    iconName: 'solar:widget-5-line-duotone',
    route: '/editor/dashboard',
    roles: ['EDITEUR']
  },

  {
    displayName: 'Mes articles',
    iconName: 'solar:document-text-line-duotone',
    route: '/editor/articles',
    roles: ['EDITEUR']
  },

  {
    displayName: 'Mes assignations',
    iconName: 'solar:users-group-two-rounded-line-duotone',
    route: '/editor/assignments',
    roles: ['EDITEUR']
  },

  {
    displayName: 'Annonces',
    iconName: 'solar:bell-line-duotone',
    route: '/editor/announcements',
    roles: ['EDITEUR']
  },

  {
    displayName: 'Mon profil',
    iconName: 'solar:user-circle-line-duotone',
    route: '/editor/profile',
    roles: ['EDITEUR']
  },


  // =========================================================
  // AUTEUR
  // =========================================================

  {
    navCap: 'ESPACE AUTEUR',
    roles: ['AUTEUR']
  },

  {
    displayName: 'Tableau de bord',
    iconName: 'solar:widget-5-line-duotone',
    route: '/author/dashboard',
    roles: ['AUTEUR']
  },

  {
    displayName: 'Mes articles',
    iconName: 'solar:document-text-line-duotone',
    route: '/author/articles',
    roles: ['AUTEUR']
  },

  {
    displayName: 'Soumettre un article',
    iconName: 'solar:file-send-line-duotone',
    route: '/author/articles/submit',
    roles: ['AUTEUR']
  },

  {
    displayName: 'Annonces',
    iconName: 'solar:bell-line-duotone',
    route: '/author/announcements',
    roles: ['AUTEUR']
  },

  {
    displayName: 'Mon profil',
    iconName: 'solar:user-circle-line-duotone',
    route: '/author/profile',
    roles: ['AUTEUR']
  },


// =========================================================
// PUBLIC
// =========================================================

{
  navCap: 'PUBLIC',
  roles: ['ALL']
},

{
  displayName: 'Publications',
  iconName: 'solar:book-2-line-duotone',
  route: '/publications',
  roles: ['ALL']
},

{
  displayName: 'Annonces',
  iconName: 'solar:bell-line-duotone',
  route: '/announcements',
  roles: ['ALL']
},


  // =========================================================
  // COMPOSANTS DU TEMPLATE
  // Réservés à l'administration
  // =========================================================

  {
    navCap: 'COMPOSANTS DU TEMPLATE',
    roles: ['ADMIN']
  },

  {
    displayName: 'Badges',
    iconName: 'solar:archive-minimalistic-line-duotone',
    route: '/ui-components/badge',
    roles: ['ADMIN']
  },

  {
    displayName: 'Formulaires',
    iconName: 'solar:notes-line-duotone',
    route: '/ui-components/forms',
    roles: ['ADMIN']
  },

  {
    displayName: 'Tables',
    iconName: 'solar:tablet-line-duotone',
    route: '/ui-components/tables',
    roles: ['ADMIN']
  },

  {
    displayName: 'Icônes',
    iconName: 'solar:sticker-smile-circle-2-line-duotone',
    route: '/extra/icons',
    roles: ['ADMIN']
  }
];