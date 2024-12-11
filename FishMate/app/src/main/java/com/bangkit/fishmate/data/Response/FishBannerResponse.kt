package com.bangkit.fishmate.data.Response

import com.google.gson.annotations.SerializedName

data class FishBannerResponse(

	@field:SerializedName("data")
	val data: BannerData? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class PenyakitItem(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null
)

data class BannerData(

	@field:SerializedName("penyakit")
	val penyakit: List<PenyakitItem?>? = null,

	@field:SerializedName("habitat")
	val habitat: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("gambar")
	val gambar: String? = null,

	@field:SerializedName("pencegahan")
	val pencegahan: String? = null
)
