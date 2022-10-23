import Vue from 'vue'

import {library} from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon, FontAwesomeLayers, FontAwesomeLayersText }
    from '@fortawesome/vue-fontawesome';

Vue.component('FontAwesomeIcon', FontAwesomeIcon);
Vue.component('FontAwesomeLayers', FontAwesomeLayers);
Vue.component('FontAwesomeLayersText', FontAwesomeLayersText);

import { faUserSecret } from '@fortawesome/free-solid-svg-icons'

library.add(faUserSecret)

