const db = require('../config/database');

const fishCollection = async () => {
    const fishList = [];

    const snapshot = await db.collection('fish').get();
    for(const doc of snapshot.docs) {
        const fishData = doc.data();
        // delete fishData.fishId;

        // const penyakit = await db.collection('fish').doc(doc.id).collection('penyakit').get();
        // const penyakitList = [];
        // penyakit.forEach((penyakitDoc) => {
        //     const penyakitData = penyakitDoc.data();
        //     penyakitList.push(penyakitData);
        //     // penyakitList.push({id: penyakitDoc.id, ...penyakitDoc.data()});         
        // });

        // fishData.penyakit = penyakitList;
        const fishName = {nama: fishData.nama};
        fishList.push(fishName);
    }
    return fishList;

}

const getFishById = async (fishId) => {
    const snapshot = await db.collection('fish').where('fishId', '==', fishId).get();

    if (snapshot.empty) {
        return null;
    };

    const doc = snapshot.docs[0];
    const fishData = doc.data();

    delete fishData.fishId;

    const penyakit = await db.collection('fish').doc(doc.id).collection('penyakit').get();
    const penyakitList = [];
    penyakit.forEach((penyakitDoc) => {
        const penyakitData = penyakitDoc.data();
        penyakitList.push(penyakitData);
    });

    fishData.penyakit = penyakitList;

    return fishData;
};

module.exports = {
    fishCollection,
    getFishById
};