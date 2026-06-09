const fs = require('fs');const path = 'src/views/user/Profile.vue';const content = fs.readFileSync(path, 'utf8');console.log('File length:', content.length); 
