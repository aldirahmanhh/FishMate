const db = require('../config/database');

const fishCollection = async () => {
    const fishList = [];

    const snapshot = await db.collection('fish').get();
    for(const doc of snapshot.docs) {
        const fishData = doc.data();
        fishData.id = doc.id;

        const penyakit = await db.collection('fish').doc(doc.id).collection('penyakit').get();
        const penyakitList = [];
        penyakit.forEach((penyakitDoc) => {
            penyakitList.push({id: penyakitDoc.id, ...penyakitDoc.data()});         
        });

        fishData.penyakit = penyakitList;
        fishList.push(fishData);
    }
    return fishList;

}

module.exports = fishCollection;